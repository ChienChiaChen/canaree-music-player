package dev.olog.msc.appwidgets.base

import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Bitmap
import android.widget.RemoteViews
import dev.olog.msc.appwidgets.BaseWidget
import dev.olog.msc.appwidgets.R
import dev.olog.msc.appwidgets.WidgetMetadata
import dev.olog.msc.core.MediaId
import dev.olog.msc.imageprovider.glide.getBitmapAsync
import dev.olog.msc.shared.ui.processor.ImageProcessor

private const val IMAGE_SIZE = 300

open class WidgetColored : BaseWidget() {

    override fun onMetadataChanged(
        context: Context,
        metadata: WidgetMetadata,
        appWidgetIds: IntArray,
        remoteViews: RemoteViews?
    ) {
        context.getBitmapAsync(MediaId.songId(metadata.id), IMAGE_SIZE) {
            val remote = remoteViews ?: RemoteViews(context.packageName, layoutId)
            remote.setTextViewText(R.id.title, metadata.title)
            remote.setTextViewText(R.id.subtitle, metadata.subtitle)

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
}