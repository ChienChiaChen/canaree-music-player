package dev.olog.msc.presentation.detail.adapter

import androidx.recyclerview.widget.DiffUtil
import dev.olog.msc.presentation.base.model.DisplayableItem

object DiffCallbackMostPlayed : DiffUtil.ItemCallback<DisplayableItem>() {
    override fun areItemsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        val sameTitle = oldItem.title == newItem.title
        val sameSubtitle = oldItem.subtitle == newItem.subtitle
        val sameIndex = oldItem.extra!!.getInt("position") == newItem.extra!!.getInt("position")
        return sameTitle && sameSubtitle && sameIndex
    }

    override fun getChangePayload(oldItem: DisplayableItem, newItem: DisplayableItem): Any? {
        if (oldItem.extra!!.getInt("position") != newItem.extra!!.getInt("position")) {
            return newItem.extra!!.getInt("position")
        }
        return super.getChangePayload(oldItem, newItem)
    }

}