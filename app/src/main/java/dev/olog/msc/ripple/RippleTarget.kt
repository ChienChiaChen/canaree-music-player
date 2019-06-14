package dev.olog.msc.ripple

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.palette.graphics.Palette
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import dev.olog.msc.presentation.widget.image.view.ForegroundImageView
import dev.olog.msc.presentation.widget.parallax.ParallaxImageView
import dev.olog.msc.utils.k.extension.getBitmap
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RippleTarget(
        imageView: ImageView,
        private val fallbackColor: Int = 0x40_606060,
        private val darkAlpha: Float = .1f,
        private val lightAlpha: Float = .2f

) : DrawableImageViewTarget(imageView) {

    private var job: Disposable? = null

    override fun onResourceReady(drawable: Drawable, transition: Transition<in Drawable>?) {
        super.onResourceReady(drawable, transition)
        if (view is ForegroundImageView) {
            job.unsubscribe()
            job = Completable.fromCallable { generateRipple(drawable) }
                .subscribeOn(Schedulers.computation())
                .subscribe({}, Throwable::printStackTrace)
        }
    }

    override fun onStop() {
        super.onStop()
        job.unsubscribe()
    }

    private fun generateRipple(drawable: Drawable) {
        val bitmap = drawable.getBitmap() ?: return
        val palette = generatePalette(bitmap)
        onGenerated(palette)
    }

    private fun generatePalette(bitmap: Bitmap) : Palette {
        return Palette.from(bitmap).clearFilters().generate()
    }

    private fun onGenerated(palette: Palette?) {
        if (view is ForegroundImageView) {

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