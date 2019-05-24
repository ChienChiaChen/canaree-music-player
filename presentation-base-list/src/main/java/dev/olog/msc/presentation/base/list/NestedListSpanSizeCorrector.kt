package dev.olog.msc.presentation.base.list

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NestedListSpanSizeCorrector(
    private val adapter: RecyclerView.Adapter<*>,
    private val layoutManager: GridLayoutManager,
    private val pageSpanCount: Int
) : RecyclerView.AdapterDataObserver(){

    init {
        updateNestedSpanCount(layoutManager, adapter.itemCount)
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        updateNestedSpanCount(layoutManager, adapter.itemCount)
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        updateNestedSpanCount(layoutManager, adapter.itemCount)
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        updateNestedSpanCount(layoutManager, adapter.itemCount)
    }

    private fun updateNestedSpanCount(layoutManager: GridLayoutManager, size: Int){
        layoutManager.spanCount = when {
            size == 0 -> 1
            size < pageSpanCount -> size
            else -> pageSpanCount
        }
    }

}