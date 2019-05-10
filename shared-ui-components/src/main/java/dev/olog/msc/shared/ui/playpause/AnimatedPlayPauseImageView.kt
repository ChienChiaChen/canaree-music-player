package dev.olog.msc.shared.ui.playpause

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.annotation.Keep
import androidx.appcompat.widget.AppCompatImageButton
import dev.olog.msc.shared.extensions.isPortrait
import dev.olog.msc.shared.ui.extensions.textColorTertiary
import dev.olog.msc.shared.ui.theme.HasDarkMode
import dev.olog.msc.shared.ui.theme.HasPlayerTheme

@Keep
class AnimatedPlayPauseImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : AppCompatImageButton(context, attrs, 0), IPlayPauseBehavior {

    private val behavior = PlayPauseBehaviorImpl(this)

    init {
        if ((context.applicationContext as HasDarkMode).isDark()){
            setColorFilter(0xFF_FFFFFF.toInt())
        }
    }

    fun setDefaultColor(){
        setColorFilter(getDefaultColor())
    }

    fun useLightImage(){
        setColorFilter(0xFF_F5F5F5.toInt())
    }

    override fun animationPlay(animate: Boolean) {
        behavior.animationPlay(animate)
    }

    override fun animationPause(animate: Boolean) {
        behavior.animationPause(animate)
    }

    private fun getDefaultColor(): Int{
        return when {
            context.isPortrait && (context.applicationContext as HasPlayerTheme).isClean() && !(context.applicationContext as HasDarkMode).isDark() -> 0xFF_8d91a6.toInt()
            (context.applicationContext as HasPlayerTheme).isFullscreen() || (context.applicationContext as HasDarkMode).isDark() -> Color.WHITE
            else -> context.textColorTertiary()
        }
    }

}
