package dev.olog.msc.presentation.recently.added

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import dev.olog.msc.presentation.base.BR
import dev.olog.msc.presentation.base.list.BasePagedAdapter
import dev.olog.msc.presentation.base.list.DataBoundViewHolder
import dev.olog.msc.presentation.base.list.DiffCallbackDisplayableItem
import dev.olog.msc.presentation.base.list.drag.TouchableAdapter
import dev.olog.msc.presentation.base.list.extensions.elevateSongOnTouch
import dev.olog.msc.presentation.base.list.extensions.setOnClickListener
import dev.olog.msc.presentation.base.list.extensions.setOnLongClickListener
import dev.olog.msc.presentation.base.list.model.DisplayableItem
import dev.olog.msc.presentation.media.MediaProvider
import dev.olog.msc.presentation.navigator.Navigator

class RecentlyAddedFragmentAdapter(
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider

) : BasePagedAdapter<DisplayableItem>(DiffCallbackDisplayableItem), TouchableAdapter {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            mediaProvider.playFromMediaId(item.mediaId)
        }
        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView)
        }
        viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
            navigator.toDialog(item.mediaId, view)
        }
        viewHolder.elevateSongOnTouch()
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }

    override fun canInteractWithViewHolder(viewType: Int): Boolean? {
        return viewType == R.layout.item_recently_added
    }

    override fun onMoved(from: Int, to: Int) {

    }

    override fun onSwipedLeft(viewHolder: RecyclerView.ViewHolder) {
        getItem(viewHolder.adapterPosition)?.let { mediaProvider.addToPlayNext(it.mediaId) }
    }

    override fun onSwipedRight(viewHolder: RecyclerView.ViewHolder) {

    }

    override fun onClearView() {

    }

}