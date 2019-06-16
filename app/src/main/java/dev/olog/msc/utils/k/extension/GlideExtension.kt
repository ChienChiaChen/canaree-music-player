package dev.olog.msc.utils.k.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Priority
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import dev.olog.msc.core.MediaId
import dev.olog.msc.glide.GlideApp
import dev.olog.msc.glide.GlideRequest
import dev.olog.msc.utils.assertBackgroundThread
import dev.olog.msc.glide.creator.CoverUtils

fun Context.getCachedBitmap(
    mediaId: MediaId,
    size: Int = Target.SIZE_ORIGINAL,
    extension: (GlideRequest<Bitmap>.() -> GlideRequest<Bitmap>)? = null,
    withError: Boolean = true
): Bitmap? {

    assertBackgroundThread()

    val placeholder = CoverUtils.getGradient(this, mediaId)

    val error = GlideApp.with(this)
        .asBitmap()
        .load(placeholder.toBitmap())
        .override(size)
        .extend(extension)

    val builder = GlideApp.with(this)
        .asBitmap()
        .load(mediaId)
        .override(size)
        .priority(Priority.IMMEDIATE)
        .onlyRetrieveFromCache(true)
        .extend(extension)

    return try {
        builder.submit().get()
    } catch (ex: Exception) {
        if (withError) {
            error.submit().get()
        } else {
            null
        }
    }

}

fun Context.getBitmap(
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
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                action(resource)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                errorDrawable?.let { action(it.toBitmap()) }
            }

            override fun onLoadCleared(placeholder: Drawable?) {

            }
        })
}

private fun GlideRequest<Bitmap>.extend(func: (GlideRequest<Bitmap>.() -> GlideRequest<Bitmap>)?): GlideRequest<Bitmap> {
    if (func != null) {
        return this.func()
    }
    return this
}