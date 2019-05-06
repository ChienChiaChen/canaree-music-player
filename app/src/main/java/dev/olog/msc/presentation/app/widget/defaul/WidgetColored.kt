package dev.olog.msc.presentation.app.widget.defaul

import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Bitmap
import android.widget.RemoteViews
import dev.olog.msc.R
import dev.olog.msc.core.MediaId
import dev.olog.msc.imageprovider.ImageModel
import dev.olog.msc.presentation.app.widget.BaseWidget
import dev.olog.msc.presentation.app.widget.WidgetMetadata
import dev.olog.msc.shared.TrackUtils
import dev.olog.msc.shared.ui.processor.ImageProcessor
import dev.olog.msc.utils.k.extension.getBitmapAsync

private const val IMAGE_SIZE = 300

open class WidgetColored : BaseWidget() {

    override fun onMetadataChanged(context: Context, metadata: WidgetMetadata, appWidgetIds: IntArray, remoteViews: RemoteViews?) {
        val model = metadata.toImageModel()
        context.getBitmapAsync(model, IMAGE_SIZE) {
            val remote = remoteViews ?: RemoteViews(context.packageName, layoutId)
            remote.setTextViewText(R.id.title, metadata.title)
            remote.setTextViewText(R.id.subtitle, TrackUtils.adjustArtist(metadata.subtitle))

            colorize(context, remote, it)

            AppWidgetManager.getInstance(context).updateAppWidget(appWidgetIds, remote)
        }
    }

    private fun colorize(context: Context, remoteViews: RemoteViews, bitmap: Bitmap) {
        val result = ImageProcessor(context).processImage(bitmap)
        remoteViews.setImageViewBitmap(R.id.cover, result.bitmap)

        updateTextColor(remoteViews, result)

        remoteViews.setInt(R.id.background, "setBackgroundColor", result.background)

        setMediaButtonColors(remoteViews, result.primaryTextColor)
    }

    override val layoutId: Int = R.layout.widget_colored

    private fun WidgetMetadata.toImageModel(): ImageModel {
        return ImageModel(
                MediaId.songId(this.id), this.image
        )
    }

}