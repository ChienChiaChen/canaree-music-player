package dev.olog.msc.presentation.library.tab

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.core.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.imageprovider.GlideApp
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.presentation.base.adapter.AbsAdapter
import dev.olog.presentation.base.adapter.DataBoundViewHolder
import dev.olog.presentation.base.extensions.elevateAlbumOnTouch
import dev.olog.presentation.base.extensions.setOnClickListener
import dev.olog.presentation.base.extensions.setOnLongClickListener
import dev.olog.presentation.base.model.DisplayableItem
import javax.inject.Inject

@PerFragment
class TabFragmentLastPlayedAlbumsAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val navigator: Navigator

): AbsAdapter<DisplayableItem>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(controller) { item, _, _ ->
            navigator.toDetailFragment(item.mediaId)
        }
        viewHolder.setOnLongClickListener(controller) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView)
        }
        viewHolder.elevateAlbumOnTouch()
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }

    override fun onViewRecycled(holder: DataBoundViewHolder) {
        holder.itemView.findViewById<View>(R.id.cover)?.let {
            GlideApp.with(holder.itemView).clear(it)
        }
        super.onViewRecycled(holder)
    }
}