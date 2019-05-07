package dev.olog.msc.presentation.related.artists

import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import dev.olog.msc.core.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.presentation.base.BR
import dev.olog.presentation.base.adapter.AbsAdapter
import dev.olog.presentation.base.adapter.DataBoundViewHolder
import dev.olog.presentation.base.extensions.elevateAlbumOnTouch
import dev.olog.presentation.base.extensions.setOnClickListener
import dev.olog.presentation.base.extensions.setOnLongClickListener
import dev.olog.presentation.base.model.DisplayableItem
import javax.inject.Inject

class RelatedArtistFragmentAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val navigator: Navigator

): AbsAdapter<DisplayableItem>(lifecycle) {


    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(controller) { item, _, _ ->
            val activity = viewHolder.itemView.context as FragmentActivity
            navigator.toDetailFragment(activity, item.mediaId)
        }
        viewHolder.setOnLongClickListener(controller) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView)
        }
        viewHolder.elevateAlbumOnTouch()
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }


}