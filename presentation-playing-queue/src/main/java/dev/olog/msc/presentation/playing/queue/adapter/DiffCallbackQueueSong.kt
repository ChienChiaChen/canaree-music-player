package dev.olog.msc.presentation.playing.queue.adapter

import androidx.recyclerview.widget.DiffUtil
import dev.olog.msc.presentation.playing.queue.model.DisplayableQueueSong

object DiffCallbackQueueSong : DiffUtil.ItemCallback<DisplayableQueueSong>() {
    override fun areItemsTheSame(oldItem: DisplayableQueueSong, newItem: DisplayableQueueSong): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(oldItem: DisplayableQueueSong, newItem: DisplayableQueueSong): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: DisplayableQueueSong, newItem: DisplayableQueueSong): Any? {
        val mutableList = mutableListOf<Any>()
        if (oldItem.positionInList != newItem.positionInList){
            mutableList.add(newItem.positionInList)
        }
        if (!oldItem.isCurrentSong && newItem.isCurrentSong){
            mutableList.add(true)
        } else if (oldItem.isCurrentSong && !newItem.isCurrentSong){
            mutableList.add(false)
        }
        if (mutableList.isNotEmpty()){
            return mutableList
        }
        return super.getChangePayload(oldItem, newItem)
    }
}