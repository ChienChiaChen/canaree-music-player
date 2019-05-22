package dev.olog.msc.presentation.tabs.adapters

import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.presentation.base.adapter.BasePagedAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.base.adapter.DiffCallbackDisplayableItem
import dev.olog.msc.presentation.base.extensions.elevateAlbumOnTouch
import dev.olog.msc.presentation.base.extensions.elevateSongOnTouch
import dev.olog.msc.presentation.base.extensions.setOnClickListener
import dev.olog.msc.presentation.base.extensions.setOnLongClickListener
import dev.olog.msc.presentation.base.interfaces.MediaProvider
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.tabs.BR
import dev.olog.msc.presentation.tabs.R
import dev.olog.msc.presentation.tabs.TabFragmentViewModel

internal class TabFragmentAdapter(
    private val mediaIdCategory: MediaIdCategory,
    private val navigator: Navigator,
    private val lastPlayedArtistsAdapter: TabFragmentLastPlayedArtistsAdapter?,
    private val lastPlayedAlbumsAdapter: TabFragmentLastPlayedAlbumsAdapter?,
    private val newAlbumsAdapter: TabFragmentNewAlbumsAdapter?,
    private val newArtistsAdapter: TabFragmentNewArtistsAdapter?,
    private val viewModel: TabFragmentViewModel,
    private val mediaProvider: MediaProvider

) : BasePagedAdapter<DisplayableItem>(DiffCallbackDisplayableItem) {

    override val placeholder: Int
        get() {
            if (mediaIdCategory == MediaIdCategory.SONGS || mediaIdCategory == MediaIdCategory.PODCASTS){
                return super.placeholder
            }
            return R.layout.item_placeholder_album
        }

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_tab_shuffle -> {
                viewHolder.setOnClickListener(this) { _, _, _ ->
                    mediaProvider.shuffle(MediaId.shuffleAllId())
                }
            }
            R.layout.item_tab_album,
            R.layout.item_tab_artist,
            R.layout.item_tab_auto_playlist,
            R.layout.item_tab_song,
            R.layout.item_tab_podcast -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    if (item.isPlayable && !item.mediaId.isPodcast) {
                        val sort = viewModel.getAllTracksSortOrder()
                        mediaProvider.playFromMediaId(item.mediaId, sort.type, sort.arranging)
                    } else if (item.isPlayable) {
                        mediaProvider.playFromMediaId(item.mediaId)
                    } else {
                        val activity = viewHolder.itemView.context as FragmentActivity
                        navigator.toDetailFragment(activity, item.mediaId)
                    }
                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    navigator.toDialog(item.mediaId, viewHolder.itemView)
                }
            }
            R.layout.item_tab_last_played_album_horizontal_list -> {
                val view = viewHolder.itemView as RecyclerView
                setupHorizontalList(view, lastPlayedAlbumsAdapter!!)
            }
            R.layout.item_tab_last_played_artist_horizontal_list -> {
                val view = viewHolder.itemView as RecyclerView
                setupHorizontalList(view, lastPlayedArtistsAdapter!!)
            }
            R.layout.item_tab_new_album_horizontal_list -> {
                val view = viewHolder.itemView as RecyclerView
                setupHorizontalList(view, newAlbumsAdapter!!)
            }
            R.layout.item_tab_new_artist_horizontal_list -> {
                val view = viewHolder.itemView as RecyclerView
                setupHorizontalList(view, newArtistsAdapter!!)
            }
        }

        when (viewType) {
            R.layout.item_tab_album,
            R.layout.item_tab_artist,
            R.layout.item_tab_auto_playlist -> viewHolder.elevateAlbumOnTouch()
            R.layout.item_tab_song,
            R.layout.item_tab_podcast -> viewHolder.elevateSongOnTouch()
        }
    }

    private fun setupHorizontalList(list: RecyclerView, adapter: BasePagedAdapter<*>) {
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            list.context,
            androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
            false
        )
        list.layoutManager = layoutManager
        list.adapter = adapter
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }

}
