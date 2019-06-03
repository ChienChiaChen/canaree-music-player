package dev.olog.msc.presentation.base.widgets

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

import dev.olog.msc.presentation.base.R

class DottedSeparator constructor(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    init {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        setBackgroundResource(R.drawable.dotted_line)
        val color = ContextCompat.getColor(context, R.color.dotted_line)
        backgroundTintList = ColorStateList.valueOf(color)
    }
}
