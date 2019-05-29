package dev.olog.msc.presentation.playing.queue.adapter

import android.view.MotionEvent
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import dev.olog.msc.app.injection.navigator.Navigator
import dev.olog.msc.presentation.base.interfaces.MediaProvider
import dev.olog.msc.presentation.base.list.BasePagedAdapter
import dev.olog.msc.presentation.base.list.DataBoundViewHolder
import dev.olog.msc.presentation.base.list.drag.OnStartDragListener
import dev.olog.msc.presentation.base.list.drag.TouchableAdapter
import dev.olog.msc.presentation.base.list.extensions.elevateSongOnTouch
import dev.olog.msc.presentation.base.list.extensions.setOnClickListener
import dev.olog.msc.presentation.base.list.extensions.setOnLongClickListener
import dev.olog.msc.presentation.playing.queue.BR
import dev.olog.msc.presentation.playing.queue.BindingAdapter
import dev.olog.msc.presentation.playing.queue.R
import dev.olog.msc.presentation.playing.queue.model.DisplayableQueueSong
import dev.olog.msc.shared.ui.extensions.textColorPrimary
import dev.olog.msc.shared.ui.extensions.textColorSecondary
import kotlinx.android.synthetic.main.item_playing_queue.view.*

class PlayingQueueFragmentAdapter(
    private val navigator: Navigator,
    private val dragListener: OnStartDragListener

) : BasePagedAdapter<DisplayableQueueSong>(DiffCallbackQueueSong), TouchableAdapter {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            val mediaProvider = viewHolder.itemView.context as MediaProvider
            mediaProvider.skipToQueueItem(item.mediaId.leaf!!)
        }

        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView)
        }
        viewHolder.itemView.findViewById<View>(R.id.more)?.setOnTouchListener { v, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                dragListener.onStartDrag(viewHolder)
            }
            false
        }
        viewHolder.elevateSongOnTouch()
    }

    override fun onBindViewHolder(holder: DataBoundViewHolder, position: Int, payloads: MutableList<Any>) {
        var updated = false
        if (payloads.isNotEmpty()){
            val newIndex = payloads.find { it is String } as String?
            if (newIndex != null){
                holder.itemView.index.text = newIndex
                updated = true
            }
            val selection = payloads.find { it is Boolean } as Boolean?
            if (selection != null){
                BindingAdapter.setBoldIfTrue(holder.itemView.firstText, selection)
                updated = true
            }
        }
        if (!updated){
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableQueueSong, position: Int) {
        binding.setVariable(BR.item, item)

        val view = binding.root
        val textColor = if (item.positionInList.startsWith("-"))
            view.context.textColorSecondary() else view.context.textColorPrimary()
        binding.root.index.setTextColor(textColor)
    }

    override fun canInteractWithViewHolder(viewType: Int): Boolean? {
        return viewType == R.layout.item_playing_queue
    }

    override fun onMoved(from: Int, to: Int) {
        //        mediaProvider.swap(from, to) TODO
    }

    override fun onSwipedLeft(viewHolder: RecyclerView.ViewHolder) {

    }

    override fun onSwipedRight(viewHolder: RecyclerView.ViewHolder) {
        //        mediaProvider.remove(position) TODO
    }

    override fun onClearView() {

    }

}