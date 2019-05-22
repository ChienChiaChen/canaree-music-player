package dev.olog.msc.presentation.player.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.media.session.PlaybackStateCompat
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageButton
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import dev.olog.msc.presentation.player.R
import dev.olog.msc.shared.ui.extensions.colorControlNormal
import dev.olog.msc.shared.ui.extensions.colorSecondary
import dev.olog.msc.shared.ui.extensions.getAnimatedVectorDrawable

class ShuffleButton : AppCompatImageButton {

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, androidx.appcompat.R.attr.imageButtonStyle)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private var enabledColor: Int = 0
    private var shuffleMode = PlaybackStateCompat.SHUFFLE_MODE_NONE

    private fun init(attrs: AttributeSet?) {
        setImageResource(R.drawable.vd_shuffle)
        enabledColor = context.colorSecondary()
        setColorFilter(getDefaultColor())
    }

    fun cycle(state: Int) {
        if (this.shuffleMode != state) {
            this.shuffleMode = state
            when (state) {
                PlaybackStateCompat.SHUFFLE_MODE_NONE -> disable()
                else -> enable()
            }
        }
    }

    fun updateSelectedColor(color: Int) {
        this.enabledColor = color

        if (shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL) {
            setColorFilter(this.enabledColor)
        }
    }

    private fun enable() {
        alpha = 1f
        animateAvd(enabledColor)
    }

    private fun disable() {
        val color = getDefaultColor()
        animateAvd(color)
    }

    private fun animateAvd(@ColorInt endColor: Int) {
        val hideDrawable = context.getAnimatedVectorDrawable(R.drawable.shuffle_hide)
        setImageDrawable(hideDrawable)
        hideDrawable.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable?) {
                val showDrawable = context.getAnimatedVectorDrawable(R.drawable.shuffle_show)
                setColorFilter(endColor)
                setImageDrawable(showDrawable)
                showDrawable.start()
            }
        })
        hideDrawable.start()
    }

    private fun getDefaultColor(): Int {
        return when {
//            context.isClean() && !context.isDark() -> 0xFF_8d91a6.toInt() // TODO get color from res
//            context.isFullscreen() -> Color.WHITE
            else -> context.colorControlNormal()
        }
    }

}