package dev.olog.msc.shared.ui.ripple

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import dev.olog.msc.shared.R
import dev.olog.msc.shared.ui.extensions.getBitmap
import dev.olog.msc.shared.ui.imageview.ForegroundImageView
import dev.olog.msc.shared.ui.imageview.ParallaxImageView
import java.lang.ref.WeakReference

class RippleTarget(
        view: ImageView,
        private val isLeaf : Boolean

) : DrawableImageViewTarget(view), Palette.PaletteAsyncListener {

    private val imageView = WeakReference(view)

    init {
        if (isLeaf && view is ForegroundImageView){
            view.foreground = null
        }
    }

    override fun onResourceReady(drawable: Drawable, transition: Transition<in Drawable>?) {
        super.onResourceReady(drawable, transition)

        if (!isLeaf && imageView.get() is ForegroundImageView){
            val bitmap = drawable.getBitmap() ?: return
            androidx.palette.graphics.Palette.from(bitmap).clearFilters().generate(this)
        }
    }

    @SuppressLint("NewApi")
    override fun onGenerated(palette: androidx.palette.graphics.Palette?) {
        val view = imageView.get() ?: return

        if (!isLeaf && view is ForegroundImageView){
            val fallbackColor = ContextCompat.getColor(view.context, R.color.mid_grey)
            val darkAlpha = .1f
            val lightAlpha = .2f

            view.foreground = RippleUtils.create(palette, darkAlpha,
                    lightAlpha, fallbackColor, true)

            if (view is ParallaxImageView){
                view.setScrimColor(RippleUtils.createColor(palette, darkAlpha,
                        lightAlpha, fallbackColor))
            }
        }
    }
}