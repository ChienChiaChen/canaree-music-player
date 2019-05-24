package dev.olog.msc.presentation.base.list.drag

import androidx.recyclerview.widget.RecyclerView


interface OnStartDragListener {
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
}