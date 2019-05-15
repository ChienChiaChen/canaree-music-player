package dev.olog.msc.presentation.detail.adapter

import androidx.databinding.ViewDataBinding
import dev.olog.msc.presentation.base.adapter.BasePagedAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.base.adapter.DiffCallbackDisplayableItem
import dev.olog.msc.presentation.base.extensions.elevateAlbumOnTouch
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.detail.BR
import dev.olog.msc.presentation.navigator.Navigator

internal class DetailRelatedArtistsAdapter(
        private val navigator: Navigator

) : BasePagedAdapter<DisplayableItem>(DiffCallbackDisplayableItem) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
//        viewHolder.setOnClickListener(controller) { item, _, _ ->
//            val activity = viewHolder.itemView.context as FragmentActivity
//            navigator.toDetailFragment(activity, item.mediaId)
//        }
//        viewHolder.setOnLongClickListener(controller) { item, _, _ ->
//            navigator.toDialog(item.mediaId, viewHolder.itemView)
//        }
        viewHolder.elevateAlbumOnTouch()
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }
}