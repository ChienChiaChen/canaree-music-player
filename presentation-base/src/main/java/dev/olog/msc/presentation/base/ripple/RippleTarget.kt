package dev.olog.msc.presentation.base.ripple

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import dev.olog.msc.core.coroutines.CustomScope
import dev.olog.msc.presentation.base.widgets.image.view.ParallaxImageView
import dev.olog.msc.shared.ui.R
import dev.olog.msc.shared.ui.extensions.getBitmap
import dev.olog.msc.shared.ui.imageview.ForegroundImageView
import kotlinx.coroutines.*
import java.lang.ref.WeakReference
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RippleTarget(
    view: ImageView,
    private val isLeaf: Boolean

) : DrawableImageViewTarget(view), CoroutineScope by CustomScope() {

    private val imageView = WeakReference(view)
    private var job: Job? = null

    init {
        if (isLeaf && view is ForegroundImageView) {
            view.foreground = null
        }
    }

    override fun onResourceReady(drawable: Drawable, transition: Transition<in Drawable>?) {
        super.onResourceReady(drawable, transition)

        if (!isLeaf && imageView.get() is ForegroundImageView) {
            job?.cancel()
            job = GlobalScope.launch {
                withTimeout(500) { generateRipple(drawable) }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        job?.cancel()
    }

    private suspend fun generateRipple(drawable: Drawable) {
        val bitmap = drawable.getBitmap() ?: return
        yield()
        val palette = generatePalette(bitmap)
        yield()
        onGenerated(palette)
    }

    private suspend fun generatePalette(bitmap: Bitmap) = suspendCoroutine<Palette?> { continuation ->
        Palette.from(bitmap).clearFilters().generate {
            continuation.resume(it)
        }
    }

    private fun onGenerated(palette: Palette?) {
        val view = imageView.get() ?: return

        if (!isLeaf && view is ForegroundImageView) {
            val fallbackColor = ContextCompat.getColor(view.context, R.color.mid_grey)
            val darkAlpha = .1f
            val lightAlpha = .2f

            view.foreground = RippleUtils.create(
                palette, darkAlpha,
                lightAlpha, fallbackColor, true
            )

            if (view is ParallaxImageView) {
                view.setScrimColor(RippleUtils.createColor(palette, darkAlpha, lightAlpha, fallbackColor))
            }
        }
    }
}