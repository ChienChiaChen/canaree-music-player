package dev.olog.msc.presentation.tabs.widgets

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import dev.olog.msc.shared.ui.extensions.windowBackground

class SideBarFade @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null
) : View(context, attrs) {

    init {
        val gradient = background as GradientDrawable
        gradient.colors = intArrayOf(Color.TRANSPARENT, windowBackground())
    }

}