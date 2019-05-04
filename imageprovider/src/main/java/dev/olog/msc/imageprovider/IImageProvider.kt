package dev.olog.msc.imageprovider

import android.content.Context
import android.graphics.Bitmap

interface IImageProvider {
    companion object {
        const val ORIGINAL_SIZE = Int.MIN_VALUE
    }

    fun getBitmap(context: Context, model: ImageModel, size: Int = ORIGINAL_SIZE, withError: Boolean = true): Bitmap
    fun getBitmapAsync(context: Context, model: ImageModel, size: Int = ORIGINAL_SIZE, action: (Bitmap) -> Unit)
}