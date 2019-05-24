package dev.olog.msc.presentation.search.adapters

import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import dev.olog.msc.presentation.base.interfaces.MediaProvider
import dev.olog.msc.presentation.base.list.BasePagedAdapter
import dev.olog.msc.presentation.base.list.DataBoundViewHolder
import dev.olog.msc.presentation.base.list.DiffCallbackDisplayableItem
import dev.olog.msc.presentation.base.list.SetupNestedList
import dev.olog.msc.presentation.base.list.drag.TouchableAdapter
import dev.olog.msc.presentation.base.list.extensions.elevateSongOnTouch
import dev.olog.msc.presentation.base.list.extensions.setOnClickListener
import dev.olog.msc.presentation.base.list.extensions.setOnLongClickListener
import dev.olog.msc.presentation.base.list.model.DisplayableItem
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.search.BR
import dev.olog.msc.presentation.search.R
import dev.olog.msc.presentation.search.SearchFragmentViewModel

internal class SearchFragmentAdapter(
    private val navigator: Navigator,
    private val viewModel: SearchFragmentViewModel,
    private val setupNestedList: SetupNestedList

) : BasePagedAdapter<DisplayableItem>(DiffCallbackDisplayableItem), TouchableAdapter {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_search_albums_horizontal_list,
            R.layout.item_search_artists_horizontal_list,
            R.layout.item_search_folder_horizontal_list,
            R.layout.item_search_playlists_horizontal_list,
            R.layout.item_search_genre_horizontal_list -> {
                val list = viewHolder.itemView as RecyclerView
                setupNestedList.setupNestedList(viewType, list)
            }
            R.layout.item_search_song -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    val mediaProvider = viewHolder.itemView.context as MediaProvider
                    mediaProvider.playFromMediaId(item.mediaId)
                    viewModel.insertToRecent(item.mediaId)

                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    navigator.toDialog(item.mediaId, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
                    navigator.toDialog(item.mediaId, view)
                }

            }
            R.layout.item_search_clear_recent -> {
                viewHolder.setOnClickListener(this) { _, _, _ ->
                    viewModel.clearRecentSearches()
                }
            }
            R.layout.item_search_recent,
            R.layout.item_search_recent_album,
            R.layout.item_search_recent_artist -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    if (item.isPlayable) {
                        val mediaProvider = viewHolder.itemView.context as MediaProvider
                        mediaProvider.playFromMediaId(item.mediaId)
                    } else {
                        val activity = viewHolder.itemView.context as FragmentActivity
                        navigator.toDetailFragment(activity, item.mediaId)
                    }
                    viewModel.insertToRecent(item.mediaId)
                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    navigator.toDialog(item.mediaId, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.clear, this) { item, _, _ ->
                    viewModel.deleteFromRecent(item.mediaId)
                }
            }
        }
        when (viewType) {
            R.layout.item_search_song,
            R.layout.item_search_recent,
            R.layout.item_search_recent_album,
            R.layout.item_search_recent_artist -> viewHolder.elevateSongOnTouch()
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }

    override fun canInteractWithViewHolder(viewType: Int): Boolean? {
        return viewType == R.layout.item_search_song ||
                viewType == R.layout.item_search_recent
    }

    override fun onMoved(from: Int, to: Int) {

    }

    override fun onSwipedLeft(viewHolder: RecyclerView.ViewHolder) {
        getItem(viewHolder.adapterPosition)?.let {
            val mediaProvider = viewHolder.itemView.context as MediaProvider
            mediaProvider.addToPlayNext(it.mediaId)
        }
    }

    override fun onSwipedRight(viewHolder: RecyclerView.ViewHolder) {

    }

    override fun onClearView() {

    }

}