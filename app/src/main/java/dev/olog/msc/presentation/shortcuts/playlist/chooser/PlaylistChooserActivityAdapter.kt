package dev.olog.msc.presentation.shortcuts.playlist.chooser

import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.core.AppShortcuts
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.base.extensions.setOnClickListener
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.shared.ui.ThemedDialog
import javax.inject.Inject

class PlaylistChooserActivityAdapter @Inject constructor(
        private val activity: FragmentActivity,
        private var appShortcuts: AppShortcuts

) : AbsAdapter<DisplayableItem>(activity.lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(controller) { item, _, _ ->
            askConfirmation(item)
        }
    }

    private fun askConfirmation(item: DisplayableItem) {
        ThemedDialog.builder(activity)
                .setTitle(R.string.playlist_chooser_dialog_title)
                .setMessage(activity.getString(R.string.playlist_chooser_dialog_message, item.title))
                .setPositiveButton(R.string.common_ok) { _, _ ->
                    appShortcuts.addDetailShortcut(item.mediaId, item.title, item.image)
                    activity.finish()
                }
            .setNegativeButton(R.string.common_no, null)
                .show()
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }
}