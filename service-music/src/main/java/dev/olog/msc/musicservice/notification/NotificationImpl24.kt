package dev.olog.msc.musicservice.notification

import android.app.NotificationManager
import android.app.Service
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.text.SpannableString
import androidx.annotation.RequiresApi
import dagger.Lazy
import dev.olog.msc.core.Classes
import dev.olog.msc.core.MediaId
import dev.olog.msc.imageprovider.glide.getCachedBitmap
import dev.olog.msc.shared.utils.TextUtils
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.N)
internal open class NotificationImpl24 @Inject constructor(
    service: Service,
    token: MediaSessionCompat.Token,
    notificationManager: Lazy<NotificationManager>,
    classes: Classes

) : NotificationImpl21(service, token, notificationManager, classes) {

    override fun startChronometer(bookmark: Long) {
        builder.setWhen(System.currentTimeMillis() - bookmark)
            .setShowWhen(true)
            .setUsesChronometer(true)
        builder.setSubText(null)
    }

    override fun stopChronometer(bookmark: Long) {
        builder.setWhen(0)
            .setShowWhen(false)
            .setUsesChronometer(false)

        builder.setSubText(TextUtils.formatMillis(bookmark, true))
    }

    override suspend fun updateMetadataImpl(
        id: Long,
        title: SpannableString,
        artist: String,
        album: String
    ) {

        val bitmap = service.getCachedBitmap(MediaId.songId(id), size = INotification.IMAGE_SIZE)
        builder.setLargeIcon(bitmap)
            .setContentTitle(title)
            .setContentText(artist)
    }

}