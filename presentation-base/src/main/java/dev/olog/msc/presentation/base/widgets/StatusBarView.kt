package dev.olog.msc.presentation.base.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import dev.olog.msc.presentation.base.extensions.hasNotch
import dev.olog.msc.shared.extensions.dimen
import dev.olog.msc.shared.extensions.dip
import dev.olog.msc.shared.extensions.isPortrait
import dev.olog.msc.shared.ui.R
import dev.olog.msc.shared.ui.theme.HasImmersive

/**
 * Custom status bar to handle device notch
 */
class StatusBarView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : View(context, attrs) {

    private val defaultStatusBarHeight = context.dimen(R.dimen.status_bar)
    private val statusBarHeightPlusNotch = context.dip(48)
    private var hasNotch = false

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        hasNotch = this.hasNotch() && context.isPortrait
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = when {
            (context.applicationContext as HasImmersive).isEnabled() -> 0
            hasNotch -> statusBarHeightPlusNotch
            else -> defaultStatusBarHeight
        }

        setMeasuredDimension(widthMeasureSpec, height)
    }

}