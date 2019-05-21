package dev.olog.msc.imageprovider.glide

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Priority
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import dev.olog.msc.core.MediaId
import dev.olog.msc.imageprovider.CoverUtils
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun Context.getCachedBitmap(
    mediaId: MediaId,
    size: Int = Target.SIZE_ORIGINAL,
    extension: (GlideRequest<Bitmap>.() -> GlideRequest<Bitmap>)? = null,
    withError: Boolean = true
): Bitmap? = suspendCoroutine { continuation ->

    val placeholder = CoverUtils.getGradient(this, mediaId)

    val error = GlideApp.with(this)
        .asBitmap()
        .load(placeholder.toBitmap())
        .override(size)
        .extend(extension)

    GlideApp.with(this)
        .asBitmap()
        .load(mediaId)
        .override(size)
        .priority(Priority.IMMEDIATE)
        .extend(extension)
        .onlyRetrieveFromCache(true)
        .into(object : SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                continuation.resume(resource)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                if (withError) {
                    error.into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            continuation.resume(resource)
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            continuation.resume(null)
                        }
                    })

                } else {
                    continuation.resume(null)
                }
            }
        })

}

fun Context.getBitmapAsync(
    mediaId: MediaId,
    size: Int = Target.SIZE_ORIGINAL,
    action: (Bitmap) -> Unit
) {

    val placeholder = CoverUtils.getGradient(this, mediaId)

    val error = GlideApp.with(this)
        .asBitmap()
        .load(placeholder.toBitmap())
        .override(size)

    GlideApp.with(this)
        .asBitmap()
        .load(mediaId)
        .error(error)
        .override(size)
        .priority(Priority.IMMEDIATE)
        .into(object : SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                action(resource)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                errorDrawable?.let { action(it.toBitmap()) }
            }
        })
}

private fun GlideRequest<Bitmap>.extend(func: (GlideRequest<Bitmap>.() -> GlideRequest<Bitmap>)?): GlideRequest<Bitmap> {
    if (func != null) {
        return this.func()
    }
    return this
}