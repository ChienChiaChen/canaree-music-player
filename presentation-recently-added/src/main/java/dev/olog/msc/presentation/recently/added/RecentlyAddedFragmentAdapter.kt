package dev.olog.msc.presentation.recently.added

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import dev.olog.msc.core.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.base.BR
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.base.extensions.elevateSongOnTouch
import dev.olog.msc.presentation.base.extensions.setOnClickListener
import dev.olog.msc.presentation.base.extensions.setOnLongClickListener
import dev.olog.msc.presentation.base.interfaces.MediaProvider
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.navigator.Navigator

class RecentlyAddedFragmentAdapter(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val navigator: Navigator,
        private val mediaProvider: MediaProvider

) : AbsAdapter<DisplayableItem>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(controller) { item, _, _ ->
            mediaProvider.playFromMediaId(item.mediaId)
        }
        viewHolder.setOnLongClickListener(controller) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView)
        }
        viewHolder.setOnClickListener(R.id.more, controller) { item, _, view ->
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

    override val onSwipeLeftAction = { position: Int ->
        controller.getItem(position)?.let { mediaProvider.addToPlayNext(it.mediaId) } ?: Any()
    }

}