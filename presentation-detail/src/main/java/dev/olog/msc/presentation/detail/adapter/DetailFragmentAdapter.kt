package dev.olog.msc.presentation.detail.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding2.view.RxView
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.sort.SortArranging
import dev.olog.msc.core.entity.sort.SortType
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.presentation.base.adapter.BasePagedAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.base.adapter.DiffCallbackDisplayableItem
import dev.olog.msc.presentation.base.extensions.elevateSongOnTouch
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.detail.BR
import dev.olog.msc.presentation.detail.DetailFragmentViewModel
import dev.olog.msc.presentation.detail.DetailFragmentViewModel.Companion.NESTED_SPAN_COUNT
import dev.olog.msc.presentation.detail.R
import dev.olog.msc.presentation.detail.Tutorial
import dev.olog.msc.presentation.navigator.Navigator
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.item_detail_header_all_song.view.*

internal class DetailFragmentAdapter (
    private val mediaId: MediaId,
    private val recentlyAddedAdapter: DetailRecentlyAddedAdapter,
    private val mostPlayedAdapter: DetailMostPlayedAdapter,
    private val relatedArtistsAdapter: DetailRelatedArtistsAdapter,
    private val albumsAdapter: DetailAlbumsAdapter,
    private val navigator: Navigator,
    private val viewModel: DetailFragmentViewModel

) : BasePagedAdapter<DisplayableItem>(DiffCallbackDisplayableItem) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int){
        when (viewType) {

            R.layout.item_detail_most_played_list -> {
                val list = viewHolder.itemView as RecyclerView
                setupHorizontalListAsGrid(list, mostPlayedAdapter)
            }
            R.layout.item_detail_recently_added_list -> {
                val list = viewHolder.itemView as RecyclerView
                setupHorizontalListAsGrid(list, recentlyAddedAdapter)
            }
            R.layout.item_detail_related_artists_list -> {
                val list = viewHolder.itemView as RecyclerView
                setupHorizontalListAsList(list, relatedArtistsAdapter)
            }
            R.layout.item_detail_albums_list -> {
                val list = viewHolder.itemView as RecyclerView
                setupHorizontalListAsList(list, albumsAdapter)
            }
            R.layout.item_detail_song,
            R.layout.item_detail_song_with_track,
            R.layout.item_detail_song_with_drag_handle,
            R.layout.item_detail_song_with_track_and_image -> {
//                viewHolder.setOnClickListener(controller) { item, _, _ ->
//                    val mediaProvider = viewHolder.itemView.context as MediaProvider
//                    viewModel.detailSortDataUseCase(item.mediaId) {
//                        mediaProvider.playFromMediaId(item.mediaId, it.sortType, it.sortArranging)
//                    }
//                }
//                viewHolder.setOnLongClickListener(controller) { item, _, _ ->
//                    navigator.toDialog(item.mediaId, viewHolder.itemView)
//                }
//                viewHolder.setOnClickListener(R.id.more, controller) { item, _, view ->
//                    navigator.toDialog(item.mediaId, view)
//                }
//                viewHolder.setOnMoveListener(controller, touchHelper)
            }
            R.layout.item_detail_shuffle -> {
//                viewHolder.setOnClickListener(controller) { _, _, _ ->
//                    val mediaProvider = viewHolder.itemView.context as MediaProvider
//                    mediaProvider.shuffle(mediaId)
//                }
            }

            R.layout.item_detail_header_recently_added -> {
//                viewHolder.setOnClickListener(R.id.seeMore, controller) { _, _, _ ->
//                    val activity = viewHolder.itemView.context as FragmentActivity
//                    navigator.toRecentlyAdded(activity, mediaId)
//                }
            }
            R.layout.item_detail_header -> {

//                viewHolder.setOnClickListener(R.id.seeMore, controller) { item, _, _ ->
//                    val activity = viewHolder.itemView.context as FragmentActivity
//                    when (item.mediaId) {
//                        DetailFragmentHeaders.RELATED_ARTISTS_SEE_ALL -> navigator.toRelatedArtists(activity, mediaId)
//                    }
//                }
            }

            R.layout.item_detail_header_all_song -> {
//                viewHolder.setOnClickListener(R.id.sort, controller) { _, _, view ->
//                    viewModel.observeSortOrder {
//                        DetailSortDialog().show(view.context, view, mediaId, it, viewModel::updateSortOrder)
//                    }
//                }
//                viewHolder.setOnClickListener(R.id.sortImage, controller) { _, _, _ ->
//                    viewModel.toggleSortArranging()
//                }
            }
        }

        when (viewType){
            R.layout.item_detail_song,
            R.layout.item_detail_song_with_track,
            R.layout.item_detail_song_with_drag_handle -> viewHolder.elevateSongOnTouch()
        }
    }

    private fun setupHorizontalListAsGrid(list: RecyclerView, adapter: BasePagedAdapter<*>){
        val layoutManager = androidx.recyclerview.widget.GridLayoutManager(list.context,
                NESTED_SPAN_COUNT, androidx.recyclerview.widget.GridLayoutManager.HORIZONTAL, false)
        list.layoutManager = layoutManager
        list.adapter = adapter
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(list)
    }

    private fun setupHorizontalListAsList(list: RecyclerView, adapter: BasePagedAdapter<*>){
        val layoutManager = LinearLayoutManager(list.context, LinearLayoutManager.HORIZONTAL, false)
        list.layoutManager = layoutManager
        list.adapter = adapter
    }

//    override fun onViewDetachedFromWindow(holder: DataBoundViewHolder) {
//        when (holder.itemViewType){
//            R.layout.item_detail_most_played_list -> {
//                mostPlayedAdapter.setAfterDataChanged(null)
//            }
//            R.layout.item_detail_recently_added_list -> {
//                recentlyAddedAdapter.setAfterDataChanged(null)
//            }
//        }
//    }

    override fun onViewAttachedToWindow(holder: DataBoundViewHolder) {
        when (holder.itemViewType) {
//            R.layout.item_detail_most_played_list -> {
//                val list = holder.itemView as RecyclerView
//                val layoutManager = list.layoutManager as androidx.recyclerview.widget.GridLayoutManager
//                mostPlayedAdapter.setAfterDataChanged({
//                    updateNestedSpanCount(layoutManager, it.size)
//                }, false)
//            }
//            R.layout.item_detail_recently_added_list -> {
//                val list = holder.itemView as RecyclerView
//                val layoutManager = list.layoutManager as androidx.recyclerview.widget.GridLayoutManager
//                recentlyAddedAdapter.setAfterDataChanged({
//                    updateNestedSpanCount(layoutManager, it.size)
//                }, false)
//            }
            R.layout.item_detail_header_all_song -> {
                val sortText = holder.itemView.sort
                val sortImage = holder.itemView.sortImage

                viewModel.observeSorting()
                        .takeUntil(RxView.detaches(holder.itemView))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ (sort, arranging) ->
                            if (sort == SortType.CUSTOM){
                                sortImage.setImageResource(R.drawable.vd_remove)
                            } else {
                                if (arranging == SortArranging.ASCENDING){
                                    sortImage.setImageResource(R.drawable.vd_arrow_down)
                                } else {
                                    sortImage.setImageResource(R.drawable.vd_arrow_up)
                                }
                            }

                        }, Throwable::printStackTrace)

                viewModel.showSortByTutorialIfNeverShown()
                        .subscribe({ Tutorial.sortBy(sortText, sortImage) }, {})
            }
        }
    }

    private fun updateNestedSpanCount(layoutManager: androidx.recyclerview.widget.GridLayoutManager, size: Int){
        layoutManager.spanCount = when {
            size == 0 -> 1
            size < NESTED_SPAN_COUNT -> size
            else -> NESTED_SPAN_COUNT
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int){
        binding.setVariable(BR.item, item)
    }

    val canSwipeRight : Boolean
        get() {
            if (mediaId.isPlaylist){
                val playlistId = mediaId.resolveId
                return playlistId != PlaylistGateway.LAST_ADDED_ID || !PlaylistGateway.isAutoPlaylist(playlistId)
            }
//            if (mediaId.isPodcastPlaylist){
//                val playlistId = mediaId.resolveId
//                return playlistId != PlaylistGatewayPODCAST_LAST_ADDED_ID || !PlaylistGatewayisPodcastAutoPlaylist(playlistId)
//            }
            return false
        }

//    override val onDragAction = { from: Int, to: Int -> viewModel.moveItemInPlaylist(from, to) }
//
//    override fun onSwipedRight(position: Int) {
//        onSwipeRightAction.invoke(position)
//        controller.remove(position)
//        notifyItemRemoved(position)
//    }
//
//    override val onSwipeRightAction = { position: Int ->
//        controller.getItem(position)?.let { viewModel.removeFromPlaylist(it) } ?: Any()
//    }
//
//    override val onSwipeLeftAction = { position: Int ->
//        controller.getItem(position)?.let {
////            mediaProvider.addToPlayNext(it.mediaId) TODO
//        } ?: Any()
//    }
//
//    override fun canInteractWithViewHolder(viewType: Int): Boolean? {
//        if (mediaId.isPodcastPlaylist){
//            return false
//        }
//        return viewType == R.layout.item_detail_song ||
//                viewType == R.layout.item_detail_song_with_drag_handle ||
//                viewType == R.layout.item_detail_song_with_track ||
//                viewType == R.layout.item_detail_song_with_track_and_image
//    }
}