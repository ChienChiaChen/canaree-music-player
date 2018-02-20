package dev.olog.msc.presentation.playing.queue

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.base.adp.AbsAdapter
import dev.olog.msc.presentation.base.adp.DataBoundViewHolder
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.utils.k.extension.elevateSongOnTouch
import dev.olog.msc.utils.k.extension.setOnClickListener
import dev.olog.msc.utils.k.extension.setOnLongClickListener
import kotlinx.android.synthetic.main.item_playing_queue.view.*
import javax.inject.Inject

class PlayingQueueFragmentAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val mediaProvider: MediaProvider,
        private val navigator: Navigator

) : AbsAdapter<DisplayableItem>(lifecycle) {

    private var currentPosition : Int = -1

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(controller) { item, _, _ ->
            mediaProvider.skipToQueueItem(item.trackNumber.toLong())
        }
        viewHolder.setOnLongClickListener(controller) { item, _, _ ->
            navigator.toDialog(item, viewHolder.itemView)
        }
        viewHolder.itemView.dragHandle.setOnTouchListener { _, event ->
            if(event.actionMasked == MotionEvent.ACTION_DOWN) {
                touchHelper?.startDrag(viewHolder)
                true
            } else false
        }
        viewHolder.elevateSongOnTouch()
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
//        binding.setVariable(BR.isCurrentSong, item.trackNumber.toInt() == PlayingQueueFragmentViewModel.idInPlaylist)
//        when {
//            position > currentPosition -> binding.setVariable(BR.index, "+${position - currentPosition}")
//            position < currentPosition -> binding.setVariable(BR.index, "${position - currentPosition}")
//            else -> binding.setVariable(BR.index, "-")
//        }
    }

//    fun updateCurrentPosition(trackNumber: Int) {
//        currentPosition = dataController.getItemPositionByPredicate { it.trackNumber.toInt() == trackNumber }
//    }

    override fun canInteractWithViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean? {
        return viewHolder.itemViewType == R.layout.item_playing_queue
    }

    override val onDragAction: ((from: Int, to: Int) -> Any)?
        get() = super.onDragAction /*musicController.swap(from, to) todo */

    override val onSwipeAction: ((position: Int) -> Any)?
        get() = super.onSwipeAction /*musicController.remove(position) */

}