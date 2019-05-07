package dev.olog.msc.presentation.detail


import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding2.widget.RxTextView
import dev.olog.msc.core.MediaId
import dev.olog.msc.presentation.detail.listener.HeaderVisibilityScrollListener
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.shared.extensions.deepCopy
import dev.olog.msc.shared.extensions.isLandscape
import dev.olog.msc.shared.extensions.isPortrait
import dev.olog.msc.shared.extensions.lazyFast
import dev.olog.msc.shared.ui.extensions.setVisible
import dev.olog.msc.shared.ui.extensions.toggleVisibility
import dev.olog.msc.shared.ui.theme.AppTheme
import dev.olog.presentation.base.DisplayableItemBindingAdapter
import dev.olog.presentation.base.drag.TouchHelperAdapterCallback
import dev.olog.presentation.base.extensions.*
import dev.olog.presentation.base.fragment.BaseFragment
import dev.olog.presentation.base.widgets.image.view.ShapeImageView
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class DetailFragment : BaseFragment() {

    companion object {
        const val TAG = "DetailFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        @JvmStatic
        fun newInstance(mediaId: MediaId): DetailFragment {
            return DetailFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString())
        }
    }

    @Inject lateinit var navigator: Navigator
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazyFast { viewModelProvider<DetailFragmentViewModel>(viewModelFactory) }

    private val recyclerOnScrollListener by lazyFast { HeaderVisibilityScrollListener(this) }

    private val recycledViewPool by lazyFast { RecyclerView.RecycledViewPool() }

    private val mediaId by lazyFast {
        val mediaId = arguments!!.getString(ARGUMENTS_MEDIA_ID)!!
        MediaId.fromString(mediaId)
    }

    private val mostPlayedAdapter by lazyFast { DetailMostPlayedAdapter(lifecycle, navigator) }
    private val recentlyAddedAdapter by lazyFast { DetailRecentlyAddedAdapter(lifecycle, navigator) }
    private val relatedArtistAdapter by lazyFast { DetailRelatedArtistsAdapter(lifecycle, navigator) }
    private val albumsAdapter by lazyFast { DetailAlbumsAdapter(lifecycle, navigator) }

    private val adapter by lazyFast {
        DetailFragmentAdapter(
                lifecycle, mediaId, recentlyAddedAdapter, mostPlayedAdapter, relatedArtistAdapter,
                albumsAdapter, navigator, viewModel, recycledViewPool
        )
    }

    internal var hasLightStatusBarColor by Delegates.observable(false) { _, _, new ->
        adjustStatusBarColor(new)
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(ctx)
        view.list.adapter = adapter
        view.list.setRecycledViewPool(recycledViewPool)
        view.list.setHasFixedSize(true)

        var swipeDirections = ItemTouchHelper.LEFT
        if (adapter.canSwipeRight){
            swipeDirections = swipeDirections or ItemTouchHelper.RIGHT
        }
        val callback = TouchHelperAdapterCallback(adapter, swipeDirections)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(view.list)
        adapter.touchHelper = touchHelper

        view.fastScroller.attachRecyclerView(view.list)
        view.fastScroller.showBubble(false)

        view.cover?.setVisible()

        viewModel.mostPlayedLiveData
                .subscribe(viewLifecycleOwner, mostPlayedAdapter::updateDataSet)

        viewModel.recentlyAddedLiveData
                .subscribe(viewLifecycleOwner, recentlyAddedAdapter::updateDataSet)

        viewModel.relatedArtistsLiveData
                .subscribe(viewLifecycleOwner, relatedArtistAdapter::updateDataSet)

        viewModel.albumsLiveData
                .subscribe(viewLifecycleOwner) {
                    albumsAdapter.updateDataSet(it)
                }

        viewModel.observeData()
                .subscribe(viewLifecycleOwner) { map ->
                    val copy = map.deepCopy()
                    if (copy.isEmpty()){
                        act.onBackPressed()
                    } else {
                        if (ctx.isLandscape){
                            // header in list is not used in landscape
                            copy[DetailFragmentDataType.HEADER]!!.clear()
                        }
                        adapter.updateDataSet(copy)
                    }
                }

        viewModel.itemLiveData.subscribe(viewLifecycleOwner) { item ->
            if (item.isNotEmpty()){
                headerText.text = item[0].title
                val cover = view.findViewById<View>(R.id.cover)
                if (!isPortrait() && cover is ShapeImageView){
                    DisplayableItemBindingAdapter.loadBigAlbumImage(cover, item[0])
                }
            }
        }

        RxTextView.afterTextChangeEvents(view.editText)
                .map { it.view() }
                .asLiveData()
                .subscribe(viewLifecycleOwner) { edit ->
                    val isEmpty = edit.text.isEmpty()
                    view.clear.toggleVisibility(!isEmpty, true)
                    viewModel.updateFilter(edit.text.toString())
                }
    }

    override fun onResume() {
        super.onResume()
        if (ctx.isPortrait){
            list.addOnScrollListener(recyclerOnScrollListener)
        }
        back.setOnClickListener { act.onBackPressed() }
        more.setOnClickListener { navigator.toDialog(viewModel.mediaId, more) }
        filter.setOnClickListener {
            searchWrapper.toggleVisibility(!searchWrapper.isVisible, true)
        }
        clear.setOnClickListener { editText.setText("") }
    }

    override fun onPause() {
        super.onPause()
        if (ctx.isPortrait){
            list.removeOnScrollListener(recyclerOnScrollListener)
//            list.removeItemDecoration(detailListMargin)
        }
        back.setOnClickListener(null)
        more.setOnClickListener(null)
        filter.setOnClickListener(null)
        clear.setOnClickListener(null)
    }

    fun adjustStatusBarColor(lightStatusBar: Boolean = hasLightStatusBarColor){
        if (lightStatusBar){
            setLightStatusBar()
        } else {
            removeLightStatusBar()
        }
    }

    private fun removeLightStatusBar(){
        act.window.removeLightStatusBar()
        val color = ContextCompat.getColor(ctx, R.color.detail_button_color_light)
        view?.back?.setColorFilter(color)
        more?.setColorFilter(color)
        filter?.setColorFilter(color)
    }

    private fun setLightStatusBar(){
        if (AppTheme.isDarkTheme()){
            return
        }

        act.window.setLightStatusBar()
        val color = ContextCompat.getColor(ctx, R.color.detail_button_color_dark)
        view?.back?.setColorFilter(color)
        more?.setColorFilter(color)
        filter?.setColorFilter(color)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_detail
}
