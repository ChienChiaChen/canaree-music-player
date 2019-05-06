package dev.olog.msc.appwidgets.queue

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import dev.olog.msc.appwidgets.R

import dev.olog.msc.appwidgets.WidgetSize
import dev.olog.msc.appwidgets.base.WidgetColored
import dev.olog.msc.shared.MusicConstants
import dev.olog.msc.shared.extensions.asServicePendingIntent

class WidgetColoredWithQueue : WidgetColored(){

    override val layoutId: Int = R.layout.widget_colored_with_queue

    override fun onQueueChanged(context: Context, appWidgetIds: IntArray) {
        setupQueue(context, appWidgetIds)
        AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetIds, R.id.queue)
    }

    override fun setupQueue(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val remoteViews = RemoteViews(context.packageName, R.layout.widget_colored_with_queue)
            remoteViews.setRemoteAdapter(R.id.queue, Intent(context, QueueWidgetService::class.java))

            val intent = Intent(context, classes.musicService())
            intent.action = MusicConstants.ACTION_SKIP_TO_ITEM
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            val pending = intent.asServicePendingIntent(context, PendingIntent.FLAG_UPDATE_CURRENT)
            remoteViews.setPendingIntentTemplate(R.id.queue, pending)

            AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, remoteViews)
        }
    }

    override fun onSizeChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, size: WidgetSize) {

    }

}