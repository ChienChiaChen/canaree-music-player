package dev.olog.msc.musicservice.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.graphics.Typeface
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dagger.Lazy
import dev.olog.msc.core.Classes
import dev.olog.msc.core.MediaId
import dev.olog.msc.imageprovider.glide.getCachedBitmap
import dev.olog.msc.musicservice.R
import dev.olog.msc.shared.MusicConstants
import dev.olog.msc.shared.PendingIntents
import dev.olog.msc.shared.extensions.asActivityPendingIntent
import dev.olog.msc.shared.extensions.asServicePendingIntent
import dev.olog.msc.shared.utils.assertBackgroundThread
import javax.inject.Inject

internal open class NotificationImpl21 @Inject constructor(
    protected val service: Service,
    private val token: MediaSessionCompat.Token,
    protected val notificationManager: Lazy<NotificationManager>,
    private val classes: Classes

) : INotification {

    protected var builder = NotificationCompat.Builder(service, INotification.CHANNEL_ID)

    private var isCreated = false

    private fun createIfNeeded() {
        assertBackgroundThread()
        if (isCreated) {
            return
        }

        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(token)
            .setShowActionsInCompactView(1, 2, 3)

        builder.setSmallIcon(R.drawable.vd_bird_not_singing)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setColor(ContextCompat.getColor(service, R.color.dark_grey))
            .setColorized(false)
            .setContentIntent(buildContentIntent())
            .setDeleteIntent(buildPendingIntent(PlaybackStateCompat.ACTION_STOP))
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(mediaStyle)
            .addAction(R.drawable.vd_not_favorite, "Add to Favorite", buildToggleFavoritePendingIntent())
            .addAction(
                R.drawable.vd_skip_previous,
                "Previous",
                buildPendingIntent(PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
            )
            .addAction(R.drawable.vd_pause_big, "PlayPause", buildPendingIntent(PlaybackStateCompat.ACTION_PLAY_PAUSE))
            .addAction(R.drawable.vd_skip_next, "Next", buildPendingIntent(PlaybackStateCompat.ACTION_SKIP_TO_NEXT))

        extendInitialization()

        isCreated = true
    }

    protected open fun extendInitialization() {}

    protected open fun startChronometer(bookmark: Long) {
    }

    protected open fun stopChronometer(bookmark: Long) {
    }

    override suspend fun update(state: MusicNotificationState): Notification {
        assertBackgroundThread()

        createIfNeeded()

        val title = state.title
        val artist = state.artist
        val album = state.album

        val spannableTitle = SpannableString(title)
        spannableTitle.setSpan(StyleSpan(Typeface.BOLD), 0, title.length, 0)
        updateMetadataImpl(state.id, spannableTitle, artist, album)
        updateState(state.isPlaying, state.bookmark - state.duration)
        updateFavorite(state.isFavorite)

        val notification = builder.build()
        notificationManager.get().notify(INotification.NOTIFICATION_ID, notification)
        return notification
    }

    private fun updateState(isPlaying: Boolean, bookmark: Long) {
        val action = builder.mActions[2]
//        action.actionIntent = buildPendingIntent(PlaybackStateCompat.ACTION_PLAY_PAUSE)
        action.icon = if (isPlaying) R.drawable.vd_pause_big else R.drawable.vd_play_big
        builder.setSmallIcon(if (isPlaying) R.drawable.vd_bird_singing else R.drawable.vd_bird_not_singing)
        builder.setOngoing(isPlaying)

        if (isPlaying) {
            startChronometer(bookmark)
        } else {
            stopChronometer(bookmark)
        }
    }

    private fun updateFavorite(isFavorite: Boolean) {
        assertBackgroundThread()
        val favoriteAction = builder.mActions[0]
        favoriteAction.icon = if (isFavorite) R.drawable.vd_favorite else R.drawable.vd_not_favorite
    }

    protected open suspend fun updateMetadataImpl(
        id: Long,
        title: SpannableString,
        artist: String,
        album: String
    ) {
        assertBackgroundThread()

        val start = System.currentTimeMillis()
        val bitmap = service.getCachedBitmap(MediaId.songId(id), size = INotification.IMAGE_SIZE)
        val end = System.currentTimeMillis() - start
        println("done in $end")
        builder.setLargeIcon(bitmap)
            .setContentTitle(title)
            .setContentText(artist)
            .setSubText(album)
    }

    private fun buildToggleFavoritePendingIntent(): PendingIntent {
        val intent = Intent(service, dev.olog.msc.musicservice.MusicService::class.java)
        intent.action = MusicConstants.ACTION_TOGGLE_FAVORITE
        return intent.asServicePendingIntent(service)
    }

    private fun buildContentIntent(): PendingIntent {
        val intent = Intent(service, classes.mainActivity())
        intent.action = PendingIntents.ACTION_CONTENT_VIEW
        return intent.asActivityPendingIntent(service)
    }

    private fun buildPendingIntent(action: Long): PendingIntent {
        return androidx.media.session.MediaButtonReceiver.buildMediaButtonPendingIntent(
            service, ComponentName(service, androidx.media.session.MediaButtonReceiver::class.java), action
        )
    }

    override fun cancel() {
        notificationManager.get().cancel(INotification.NOTIFICATION_ID)
    }
}