package dev.olog.msc.presentation.player

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.math.MathUtils
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dev.olog.msc.presentation.base.drag.OnStartDragListener
import dev.olog.msc.presentation.base.drag.TouchHelperAdapterCallback
import dev.olog.msc.presentation.base.extensions.ctx
import dev.olog.msc.presentation.base.extensions.distinctUntilChanged
import dev.olog.msc.presentation.base.extensions.subscribe
import dev.olog.msc.presentation.base.extensions.viewModelProvider
import dev.olog.msc.presentation.base.fragment.BaseFragment
import dev.olog.msc.presentation.base.interfaces.MediaProvider
import dev.olog.msc.presentation.base.theme.player.theme.isBigImage
import dev.olog.msc.presentation.base.theme.player.theme.isClean
import dev.olog.msc.presentation.base.theme.player.theme.isFullscreen
import dev.olog.msc.presentation.base.theme.player.theme.isMini
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.player.appearance.IPlayerAppearanceDelegate
import dev.olog.msc.shared.MusicConstants.PROGRESS_BAR_INTERVAL
import dev.olog.msc.shared.extensions.extractBookmark
import dev.olog.msc.shared.extensions.isPlaying
import dev.olog.msc.shared.extensions.lazyFast
import dev.olog.msc.shared.extensions.unsubscribe
import dev.olog.msc.shared.ui.theme.HasPlayerTheme
import dev.olog.msc.shared.utils.isMarshmallow
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.android.synthetic.main.fragment_player.view.*
import kotlinx.android.synthetic.main.fragment_player_toolbar.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs

class PlayerFragment : BaseFragment(),
        SlidingUpPanelLayout.PanelSlideListener,
        OnStartDragListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazyFast { viewModelProvider<PlayerFragmentViewModel>(viewModelFactory) }
    @Inject
    lateinit var presenter: PlayerFragmentPresenter
    @Inject
    lateinit var navigator: Navigator

    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var mediaProvider: MediaProvider

    private var seekBarDisposable: Disposable? = null

    private var lyricsDisposable: Disposable? = null

    private var itemTouchHelper: ItemTouchHelper? = null

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        val adapter = PlayerFragmentAdapter(
                viewLifecycleOwner.lifecycle,
                activity as MediaProvider,
                navigator, viewModel, presenter,
                IPlayerAppearanceDelegate.get(
                        ctx.applicationContext as HasPlayerTheme,
                        viewModel
                ),
                this
        )

        layoutManager = LinearLayoutManager(context)
        view.list.adapter = adapter
        view.list.layoutManager = layoutManager
        view.list.isNestedScrollingEnabled = false
        val callback = TouchHelperAdapterCallback(adapter, ItemTouchHelper.RIGHT/* or ItemTouchHelper.LEFT*/)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper!!.attachToRecyclerView(view.list)

        val statusBarAlpha = if (!isMarshmallow()) 1f else 0f
        view.statusBar?.alpha = statusBarAlpha

        if (context.isBigImage()) {
            val set = ConstraintSet()
            set.clone(view as ConstraintLayout)
            set.connect(view.list.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            set.applyTo(view)
        }

        mediaProvider = (activity as MediaProvider)

        mediaProvider.onQueueChanged()
                .distinctUntilChanged()
                .subscribe(viewLifecycleOwner) { viewModel.updateQueue(ctx, it) }

        viewModel.observeMiniQueue()
                .subscribe(viewLifecycleOwner, adapter::updateDataSet)

        mediaProvider.onStateChanged()
                .subscribe(viewLifecycleOwner) {
                    val bookmark = it.extractBookmark()
                    viewModel.updateProgress(bookmark)
                    handleSeekBar(bookmark, it.isPlaying(), it.playbackSpeed)
                }
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper?.startDrag(viewHolder)
    }

    private fun handleSeekBar(bookmark: Int, isPlaying: Boolean, speed: Float) {
        seekBarDisposable.unsubscribe()

        if (isPlaying) {
            seekBarDisposable = Observable.interval(PROGRESS_BAR_INTERVAL, TimeUnit.MILLISECONDS, Schedulers.computation())
                            .map { (it + 1) * PROGRESS_BAR_INTERVAL * speed + bookmark }
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ viewModel.updateProgress(it.toInt()) }, Throwable::printStackTrace)
        }
    }

    override fun onResume() {
        super.onResume()
        getSlidingPanel()?.setScrollableView(list)
        getSlidingPanel()!!.addPanelSlideListener(this)
    }

    override fun onPause() {
        super.onPause()
        getSlidingPanel()?.removePanelSlideListener(this)
    }

    override fun onStop() {
        super.onStop()
        seekBarDisposable.unsubscribe()
        lyricsDisposable.unsubscribe()
    }

    override fun onPanelSlide(panel: View?, slideOffset: Float) {
        if (!isMarshmallow() && slideOffset in .9f..1f) {
            val alpha = (1 - slideOffset) * 10
            statusBar?.alpha = MathUtils.clamp(abs(1 - alpha), 0f, 1f)
        }
    }

    override fun onPanelStateChanged(
            panel: View,
            previousState: SlidingUpPanelLayout.PanelState,
            newState: SlidingUpPanelLayout.PanelState
    ) {
        if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            if (viewModel.showLyricsTutorialIfNeverShown()) {
                lyrics?.let { Tutorial.lyrics(it) }
            }
        } else {
            lyricsDisposable.unsubscribe()
        }
    }

    override fun provideLayoutId(): Int {
        return when {
            context.isFullscreen() -> R.layout.fragment_player_fullscreen
            context.isClean() -> R.layout.fragment_player_clean
            context.isMini() -> R.layout.fragment_player_mini
            else -> R.layout.fragment_player
        }
    }
}