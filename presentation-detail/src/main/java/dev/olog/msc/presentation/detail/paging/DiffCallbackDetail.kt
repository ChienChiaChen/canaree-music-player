package dev.olog.msc.presentation.detail.paging

import androidx.recyclerview.widget.DiffUtil
import dev.olog.msc.presentation.base.model.DisplayableItem

object DiffCallbackDetail : DiffUtil.ItemCallback<DisplayableItem>(){
    override fun areItemsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: DisplayableItem, newItem: DisplayableItem): Any? {
        if (!newItem.mediaId.isLeaf && oldItem.subtitle != newItem.subtitle){
            println("old item $oldItem, new item $newItem in change playload")
            return newItem.subtitle
        }
        return super.getChangePayload(oldItem, newItem)
    }

}