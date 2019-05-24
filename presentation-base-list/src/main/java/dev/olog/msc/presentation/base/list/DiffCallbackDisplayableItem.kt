package dev.olog.msc.presentation.base.list

import androidx.recyclerview.widget.DiffUtil
import dev.olog.msc.presentation.base.list.model.DisplayableItem

object DiffCallbackDisplayableItem : DiffUtil.ItemCallback<DisplayableItem>(){
    override fun areItemsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return oldItem == newItem
    }
}