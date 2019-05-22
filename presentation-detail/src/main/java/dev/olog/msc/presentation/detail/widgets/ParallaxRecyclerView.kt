package dev.olog.msc.presentation.detail.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.olog.msc.presentation.base.widgets.image.view.ParallaxImageView
import dev.olog.msc.presentation.detail.R

class ParallaxRecyclerView : RecyclerView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode) {
            addOnScrollListener(parallaxScrollListener)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeOnScrollListener(parallaxScrollListener)
    }

    private val parallaxScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (!isInEditMode) {
                val firstVisible = findFirstVisibleItemPosition()
                if (firstVisible > 0) return

                val viewHolder = recyclerView.findViewHolderForAdapterPosition(firstVisible)
                if (viewHolder != null) {
                    val img = viewHolder.itemView.findViewById<View>(R.id.cover)
                    val textWrapper = viewHolder.itemView.findViewById<View>(R.id.textWrapper)
                    if (img != null && img is ParallaxImageView) {
                        img.translateY(viewHolder.itemView, textWrapper)
                    }
                }
            }
        }
    }

    private fun findFirstVisibleItemPosition(): Int {
        val layoutManager = layoutManager
        return when (layoutManager) {
            is LinearLayoutManager -> layoutManager.findFirstVisibleItemPosition()
            is GridLayoutManager -> layoutManager.findFirstVisibleItemPosition()
            else -> throw IllegalArgumentException("invalid layout manager class ${layoutManager!!::class}")
        }
    }

}