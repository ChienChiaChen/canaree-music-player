package dev.olog.msc.presentation.base.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import dev.olog.msc.shared.extensions.dimen
import dev.olog.msc.shared.ui.R
import dev.olog.msc.shared.ui.extensions.colorSurface
import dev.olog.msc.shared.ui.theme.immersive
import dev.olog.msc.shared.utils.isP

/**
 * Custom status bar to handleOnBackPressed device notch
 */
class StatusBarView : View {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }


    private val defaultStatusBarHeight = context.dimen(R.dimen.status_bar)
    private val statusBarHeightPlusNotch by lazy {
        if (isP()) {
            rootWindowInsets.consumeStableInsets().stableInsetTop
        } else {
            TODO("VERSION.SDK_INT < M")
        }
    }
    private val View.hasNotch by lazy {
        if (isP()){
            rootWindowInsets?.displayCutout != null
        } else {
            false
        }
    }

    private fun init() {
        setBackgroundColor(context.colorSurface())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (!isInEditMode) {
            val height = when {
                context.immersive().isEnabled() -> 0
                hasNotch -> statusBarHeightPlusNotch
                else -> defaultStatusBarHeight
            }

            setMeasuredDimension(widthMeasureSpec, height)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

}