package dev.olog.msc.presentation.playlist.chooser

import androidx.appcompat.app.AlertDialog
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import dev.olog.msc.appshortcuts.AppShortcuts
import dev.olog.msc.presentation.base.list.DataBoundViewHolder
import dev.olog.msc.presentation.base.list.SimpleAdapter
import dev.olog.msc.presentation.base.list.model.DisplayableItem

class PlaylistChooserActivityAdapter (
    private val activity: FragmentActivity

) : SimpleAdapter<DisplayableItem>() {

    private val appShortcuts = AppShortcuts.instance(activity)

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.itemView.setOnClickListener {
            val item = data[viewHolder.adapterPosition]
            askConfirmation(item)
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }

    private fun askConfirmation(item: DisplayableItem) {
        AlertDialog.Builder(activity)
            .setTitle(R.string.playlist_chooser_dialog_title)
            .setMessage(activity.getString(R.string.playlist_chooser_dialog_message, item.title))
            .setPositiveButton(R.string.common_ok) { _, _ ->
                appShortcuts.addDetailShortcut(item.mediaId, item.title)
                activity.finish()
            }
            .setNegativeButton(R.string.common_no, null)
            .show()
    }

}