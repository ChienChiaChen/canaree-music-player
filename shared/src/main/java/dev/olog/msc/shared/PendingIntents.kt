package dev.olog.msc.shared

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dev.olog.msc.shared.extensions.asServicePendingIntent

object PendingIntents {

    private const val TAG = "PendingIntents"
    const val ACTION_STOP_SLEEP_END = "$TAG.action.stop_sleep_timer"

    fun stopMusicServiceIntent(context: Context, musicService: Class<*>): PendingIntent {
        val intent = Intent(context, musicService)
        intent.action = ACTION_STOP_SLEEP_END
        return intent.asServicePendingIntent(context, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    const val ACTION_CONTENT_VIEW = "$TAG.action.content.view"

}