package dev.olog.msc.shared.ui.imageview

import android.content.Context
import android.util.AttributeSet
import android.view.ViewPropertyAnimator
import androidx.annotation.Keep
import androidx.appcompat.widget.AppCompatImageButton
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import dev.olog.msc.shared.ui.R
import dev.olog.msc.shared.ui.extensions.getAnimatedVectorDrawable

@Keep
class AnimatedImageView : AppCompatImageButton {


    private lateinit var avd: AnimatedVectorDrawableCompat
    private val animator: ViewPropertyAnimator = animate()

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initialize(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(attrs)
    }

    private fun initialize(attrs: AttributeSet?) {
//        if ((context.applicationContext as HasDarkMode).isDark()) {
//            setColorFilter(0xFF_FFFFFF.toInt())
//        }
//
        val a = context.theme.obtainStyledAttributes(
            attrs, R.styleable.AnimatedImageView, 0, 0
        )

        val resId = a.getResourceId(R.styleable.AnimatedImageView_avd, -1)
        avd = context.getAnimatedVectorDrawable(resId)
        setImageDrawable(avd)
        a.recycle()

//        when {
//            context.isPortrait && (context.applicationContext as HasPlayerTheme).isClean() && !(context.applicationContext as HasDarkMode).isDark() -> 0xFF_8d91a6.toInt()
//            (context.applicationContext as HasPlayerTheme).isFullscreen() || (context.applicationContext as HasDarkMode).isDark() -> Color.WHITE
//            else -> context.textColorTertiary()
//        } TODO
    }

    fun playAnimation() {
        stopPreviousAnimation()
        avd.start()
    }

    private fun stopPreviousAnimation() {
        avd.stop()
    }

    fun updateVisibility(show: Boolean) {
        isEnabled = show

        animator.cancel()
        animator.alpha(if (show) 1f else 0f)
    }

}
