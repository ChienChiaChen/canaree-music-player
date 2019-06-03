package dev.olog.msc.presentation.base.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.WindowInsets
import dev.olog.msc.shared.ui.extensions.colorSurface
import dev.olog.msc.shared.ui.extensions.setHeight

/**
 * Custom status bar to handleOnBackPressed device notch
 */
class StatusBarView : View {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private var viewHeight = 0

    override fun onApplyWindowInsets(insets: WindowInsets?): WindowInsets {
        viewHeight = insets?.systemWindowInsetTop ?: 0
        setHeight(viewHeight)
        return super.onApplyWindowInsets(insets)
    }

    private fun init() {
        setBackgroundColor(context.colorSurface())
    }

}