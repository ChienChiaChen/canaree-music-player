package dev.olog.msc.presentation.player.mini

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import androidx.core.math.MathUtils
import androidx.lifecycle.ViewModelProvider
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dev.olog.msc.presentation.base.extensions.expand
import dev.olog.msc.presentation.base.extensions.isCollapsed
import dev.olog.msc.presentation.base.extensions.isExpanded
import dev.olog.msc.presentation.base.extensions.viewModelProvider
import dev.olog.msc.presentation.base.fragment.BaseFragment
import dev.olog.msc.presentation.base.interfaces.MediaProvider
import dev.olog.msc.presentation.base.theme.player.theme.isMini
import dev.olog.msc.presentation.base.utils.getArtist
import dev.olog.msc.presentation.base.utils.getDuration
import dev.olog.msc.presentation.base.utils.getTitle
import dev.olog.msc.presentation.base.utils.isPodcast
import dev.olog.msc.shared.MusicConstants.PROGRESS_BAR_INTERVAL
import dev.olog.msc.shared.core.flow.flowInterval
import dev.olog.msc.shared.extensions.isPaused
import dev.olog.msc.shared.extensions.isPlaying
import dev.olog.msc.shared.extensions.lazyFast
import dev.olog.msc.shared.ui.extensions.*
import kotlinx.android.synthetic.main.fragment_mini_player.*
import kotlinx.android.synthetic.main.fragment_mini_player.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MiniPlayerFragment : BaseFragment(), SlidingUpPanelLayout.PanelSlideListener, CoroutineScope by MainScope() {

    companion object {
        private const val TAG = "MiniPlayerFragment"
        private const val BUNDLE_IS_VISIBLE = "$TAG.BUNDLE_IS_VISIBLE"
    }

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val presenter by lazyFast { viewModelProvider<MiniPlayerFragmentViewModel>(factory) }

    private var seekBarJob: Job? = null // TODO make this like a class, is also used in player fragment and floating window

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            view.toggleVisibility(it.getBoolean(BUNDLE_IS_VISIBLE), true)
        }
        val (modelTitle, modelSubtitle) = presenter.getMetadata()
        view.title.text = modelTitle
        view.artist.text = modelSubtitle

        val media = activity as MediaProvider

        view.coverWrapper.toggleVisibility(context.isMini(), true)

        media.onMetadataChanged()
            .subscribe(viewLifecycleOwner) {
                title.text = it.getTitle()
                presenter.startShowingLeftTime(it.isPodcast(), it.getDuration())
                if (!it.isPodcast()) {
                    artist.text = it.getArtist()
                }
                updateProgressBarMax(it.getDuration())
                updateImage(it)
            }

        presenter.observeProgress
            .map { resources.getQuantityString(R.plurals.mini_player_time_left, it.toInt(), it) }
            .filter { text -> view.artist.text != text }
            .subscribe(viewLifecycleOwner) {
                artist.text = it
            }

        media.onStateChanged()
            .filter { it.isPlaying() || it.isPaused() }
            .distinctUntilChanged()
            .subscribe(viewLifecycleOwner) {
                updateProgressBarProgress(it.position)
                handleProgressBar(it.isPlaying(), it.playbackSpeed)
            }

        media.onStateChanged()
            .map { it.state }
            .filter { it == PlaybackStateCompat.STATE_PLAYING || it == PlaybackStateCompat.STATE_PAUSED }
            .distinctUntilChanged()
            .subscribe(viewLifecycleOwner) { state ->

                if (state == PlaybackStateCompat.STATE_PLAYING) {
                    playAnimation(true)
                } else {
                    pauseAnimation(true)
                }
            }

        media.onStateChanged()
            .map { it.state }
            .filter { state ->
                state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT ||
                        state == PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS
            }
            .map { state -> state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT }
            .subscribe(viewLifecycleOwner, this::animateSkipTo)

//        RxView.clicks(view.next) TODO
//            .asLiveData()
//            .subscribe(viewLifecycleOwner) { media.skipToNext() }

//        RxView.clicks(view.playPause) TODO
//            .asLiveData()
//            .subscribe(viewLifecycleOwner) { media.playPause() }

//        RxView.clicks(view.previous)
//            .asLiveData()
//            .subscribe(viewLifecycleOwner) { media.skipToPrevious() }

        presenter.skipToNextVisibility
            .subscribe(viewLifecycleOwner) {
                view.next.updateVisibility(it)
            }

        presenter.skipToPreviousVisibility
            .subscribe(viewLifecycleOwner) {
                view.previous.updateVisibility(it)
            }
    }

    override fun onResume() {
        super.onResume()
        getSlidingPanel()!!.addPanelSlideListener(this)
        view?.setOnClickListener { getSlidingPanel()?.expand() }
        view?.toggleVisibility(!getSlidingPanel().isExpanded(), true)
    }

    override fun onPause() {
        super.onPause()
        getSlidingPanel()!!.removePanelSlideListener(this)
        view?.setOnClickListener(null)
    }

    override fun onStop() {
        super.onStop()
        seekBarJob?.cancel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(BUNDLE_IS_VISIBLE, getSlidingPanel().isCollapsed())
    }

    private fun playAnimation(animate: Boolean) {
        playPause.animationPlay(getSlidingPanel().isCollapsed() && animate)
    }

    private fun pauseAnimation(animate: Boolean) {
        playPause.animationPause(getSlidingPanel().isCollapsed() && animate)
    }

    private fun animateSkipTo(toNext: Boolean) {
        if (getSlidingPanel().isExpanded()) return

        if (toNext) {
            view!!.next.playAnimation()
        } else {
            view!!.previous.playAnimation()
        }
    }

    private fun updateProgressBarProgress(progress: Long) {
        view!!.progressBar.progress = progress.toInt()
    }

    private fun updateProgressBarMax(max: Long) {
        view!!.progressBar.max = max.toInt()
    }

    private fun updateImage(metadata: MediaMetadataCompat) {
        if (!context.isMini()) {
            return
        }
        bigCover.loadImage(metadata)
    }

    private fun handleProgressBar(isPlaying: Boolean, speed: Float) {
        seekBarJob?.cancel()
        if (isPlaying) {
            resumeProgressBar(speed)
        }
    }

    private fun resumeProgressBar(speed: Float) {
        seekBarJob = launch {
            flowInterval(PROGRESS_BAR_INTERVAL, TimeUnit.MILLISECONDS)
                .collect {
                    progressBar.incrementProgressBy((PROGRESS_BAR_INTERVAL * speed).toInt())
                    presenter.updateProgress((progressBar.progress + (PROGRESS_BAR_INTERVAL * speed)).toLong())
                }
        }
    }


    override fun onPanelSlide(panel: View?, slideOffset: Float) {
        view?.alpha = MathUtils.clamp(1 - slideOffset * 3f, 0f, 1f)
        view?.toggleVisibility(slideOffset <= .8f, true)
    }

    override fun onPanelStateChanged(
        panel: View?,
        previousState: SlidingUpPanelLayout.PanelState?,
        newState: SlidingUpPanelLayout.PanelState?
    ) {
    }

    override fun provideLayoutId(): Int = R.layout.fragment_mini_player
}