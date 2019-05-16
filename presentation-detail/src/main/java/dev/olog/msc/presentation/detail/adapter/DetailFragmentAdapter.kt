package dev.olog.msc.presentation.detail.adapter

import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.sort.SortArranging
import dev.olog.msc.core.entity.sort.SortType
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.presentation.base.adapter.BasePagedAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.base.adapter.DiffCallbackDisplayableItem
import dev.olog.msc.presentation.base.adapter.SetupNestedList
import dev.olog.msc.presentation.base.extensions.elevateSongOnTouch
import dev.olog.msc.presentation.base.extensions.setOnClickListener
import dev.olog.msc.presentation.base.extensions.setOnLongClickListener
import dev.olog.msc.presentation.base.extensions.subscribe
import dev.olog.msc.presentation.base.interfaces.MediaProvider
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.detail.*
import dev.olog.msc.presentation.detail.sort.DetailSortDialog
import dev.olog.msc.presentation.navigator.Navigator
import kotlinx.android.synthetic.main.item_detail_header_all_song.view.*

internal class DetailFragmentAdapter (
    private val mediaId: MediaId,
    private val mediaProvider: MediaProvider,
    private val setupNestedList: SetupNestedList,
    private val navigator: Navigator,
    private val viewModel: DetailFragmentViewModel

) : BasePagedAdapter<DisplayableItem>(DiffCallbackDisplayableItem) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int){
        when (viewType) {

            R.layout.item_detail_most_played_list,
            R.layout.item_detail_recently_added_list,
            R.layout.item_detail_related_artists_list,
            R.layout.item_detail_albums_list -> {
                setupNestedList.setupNestedList(viewType, viewHolder.itemView as RecyclerView)
            }
            R.layout.item_detail_song,
            R.layout.item_detail_song_with_track,
            R.layout.item_detail_song_with_drag_handle,
            R.layout.item_detail_song_with_track_and_image -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    viewModel.getDetailSort {
                        mediaProvider.playFromMediaId(item.mediaId, it.sortType, it.sortArranging)
                    }
                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    navigator.toDialog(item.mediaId, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
                    navigator.toDialog(item.mediaId, view)
                }
//                viewHolder.setOnMoveListener(controller, touchHelper)
            }
            R.layout.item_detail_shuffle -> {
                viewHolder.setOnClickListener(this) { _, _, _ ->
                    mediaProvider.shuffle(mediaId)
                }
            }

            R.layout.item_detail_header_recently_added -> {
                viewHolder.setOnClickListener(R.id.seeMore, this) { _, _, _ ->
                    val activity = viewHolder.itemView.context as FragmentActivity
                    navigator.toRecentlyAdded(activity, mediaId)
                }
            }
            R.layout.item_detail_header -> {
                viewHolder.setOnClickListener(R.id.seeMore, this) { item, _, _ ->
                    val activity = viewHolder.itemView.context as FragmentActivity
                    when (item.mediaId) {
                        DetailFragmentHeaders.RELATED_ARTISTS_SEE_ALL -> navigator.toRelatedArtists(activity, mediaId)
                    }
                }
            }

            R.layout.item_detail_header_all_song -> {
                val sortText = viewHolder.itemView.sort
                val sortImage = viewHolder.itemView.sortImage

                viewModel.observeSorting()
                    .subscribe(viewHolder) { (sort, arranging) ->
                        if (sort == SortType.CUSTOM){
                            sortImage.setImageResource(R.drawable.vd_remove)
                        } else {
                            if (arranging == SortArranging.ASCENDING){
                                sortImage.setImageResource(R.drawable.vd_arrow_down)
                            } else {
                                sortImage.setImageResource(R.drawable.vd_arrow_up)
                            }
                        }
                    }

                viewModel.canShowSortByTutorial { Tutorial.sortBy(sortText, sortImage) }

                viewHolder.setOnClickListener(R.id.sort, this) { _, _, view ->
                    viewModel.getDetailSort { detailSort ->
                        DetailSortDialog().show(view.context, view, mediaId, detailSort.sortType) {
                            viewModel.updateSortOrder(it)
                        }
                    }
                }
                viewHolder.setOnClickListener(R.id.sortImage, this) { _, _, _ ->
                    viewModel.toggleSortArranging()
                }
            }
        }

        when (viewType){
            R.layout.item_detail_song,
            R.layout.item_detail_song_with_track,
            R.layout.item_detail_song_with_drag_handle -> viewHolder.elevateSongOnTouch()
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