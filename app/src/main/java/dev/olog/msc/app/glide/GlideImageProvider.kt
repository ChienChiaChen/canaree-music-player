package dev.olog.msc.app.glide

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Priority
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import dev.olog.msc.imageprovider.CoverUtils
import dev.olog.msc.imageprovider.IImageProvider
import dev.olog.msc.imageprovider.ImageModel
import dev.olog.msc.imageprovider.ImageUtils
import dev.olog.msc.shared.utils.assertBackgroundThread
import javax.inject.Inject

class GlideImageProvider @Inject constructor() : IImageProvider {

    override fun getBitmap(
            context: Context,
            model: ImageModel,
            size: Int,
            withError: Boolean): Bitmap {

        assertBackgroundThread()

        val placeholder = CoverUtils.getGradient(context, model.mediaId)

        val onlyFromCache = !ImageUtils.isRealImage(model.image)
        val load : Any = if (!onlyFromCache) model.image else model

        val imageSize = if (size == IImageProvider.ORIGINAL_SIZE) Target.SIZE_ORIGINAL else size

        val error = GlideApp.with(context)
                .asBitmap()
                .load(placeholder.toBitmap())
                .override(imageSize)

        val builder = GlideApp.with(context)
                .asBitmap()
                .load(load)
                .override(imageSize)
                .priority(Priority.IMMEDIATE)
                .onlyRetrieveFromCache(onlyFromCache)

        return try {
            builder.submit().get()
        } catch (ex: Exception){
            if (withError){
                error.submit().get()
            } else {
                throw NullPointerException()
            }
        }

    }

    override fun getBitmapAsync(
            context: Context,
            model: ImageModel,
            size: Int,
            action: (Bitmap) -> Unit
    ){

        val placeholder = CoverUtils.getGradient(context, model.mediaId)

        val onlyFromCache = !ImageUtils.isRealImage(model.image)
        val load : Any = if (!onlyFromCache) model.image else model

        val imageSize = if (size == IImageProvider.ORIGINAL_SIZE) Target.SIZE_ORIGINAL else size

        val error = GlideApp.with(context)
                .asBitmap()
                .load(placeholder.toBitmap())
                .override(imageSize)


        GlideApp.with(context)
                .asBitmap()
                .load(load)
                .error(error)
                .override(imageSize)
                .priority(Priority.IMMEDIATE)
                .onlyRetrieveFromCache(onlyFromCache)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        action(resource)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        errorDrawable?.let { action(it.toBitmap()) }
                    }
                })
    }
}