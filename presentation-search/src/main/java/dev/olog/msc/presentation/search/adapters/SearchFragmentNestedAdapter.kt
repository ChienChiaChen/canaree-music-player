package dev.olog.msc.presentation.search.adapters

import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import dev.olog.msc.presentation.base.list.BasePagedAdapter
import dev.olog.msc.presentation.base.list.DataBoundViewHolder
import dev.olog.msc.presentation.base.list.DiffCallbackDisplayableItem
import dev.olog.msc.presentation.base.list.extensions.elevateAlbumOnTouch
import dev.olog.msc.presentation.base.list.extensions.setOnClickListener
import dev.olog.msc.presentation.base.list.extensions.setOnLongClickListener
import dev.olog.msc.presentation.base.list.model.DisplayableItem
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.search.BR
import dev.olog.msc.presentation.search.SearchFragmentViewModel
import javax.inject.Inject

internal class SearchFragmentNestedAdapter @Inject constructor(
    private val navigator: Navigator,
    private val viewModel: SearchFragmentViewModel

) : BasePagedAdapter<DisplayableItem>(DiffCallbackDisplayableItem) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            val activity = viewHolder.itemView.context as FragmentActivity
            navigator.toDetailFragment(activity, item.mediaId)
            viewModel.insertToRecent(item.mediaId)
        }
        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView)
        }
        viewHolder.elevateAlbumOnTouch()
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }

}