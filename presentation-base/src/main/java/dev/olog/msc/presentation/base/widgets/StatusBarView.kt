package dev.olog.msc.presentation.base.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import dev.olog.msc.presentation.base.extensions.hasNotch
import dev.olog.msc.shared.extensions.dimen
import dev.olog.msc.shared.extensions.dip
import dev.olog.msc.shared.ui.R
import dev.olog.msc.shared.ui.extensions.colorPrimary
import dev.olog.msc.shared.ui.theme.HasImmersive

/**
 * Custom status bar to handle device notch
 */
class StatusBarView : View {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }


    private val defaultStatusBarHeight = context.dimen(R.dimen.status_bar)
    private val statusBarHeightPlusNotch = context.dip(48)
    private var hasNotch = false

    private fun init() {
        setBackgroundColor(context.colorPrimary())
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode) {
            hasNotch = this.hasNotch()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (!isInEditMode) {
            val height = when {
                (context.applicationContext as HasImmersive).isEnabled() -> 0
                hasNotch -> statusBarHeightPlusNotch
                else -> defaultStatusBarHeight
            }

            setMeasuredDimension(widthMeasureSpec, height)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

}