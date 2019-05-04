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
import dev.olog.msc.imageprovider.IImageProvider
import dev.olog.msc.imageprovider.ImageModel
import dev.olog.msc.shared.utils.TextUtils
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.N)
open class NotificationImpl24 @Inject constructor(
        service: Service,
        token: MediaSessionCompat.Token,
        notificationManager: Lazy<NotificationManager>,
        classes: Classes,
        imageProvider: IImageProvider

) : NotificationImpl21(service, token, notificationManager, classes, imageProvider) {

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

    override fun updateMetadataImpl(
            id: Long,
            title: SpannableString,
            artist: String,
            album: String,
            image: String) {

        val model = ImageModel(MediaId.songId(id), image)
        val bitmap = imageProvider.getBitmap(service, model, size = INotification.IMAGE_SIZE)
        builder.setLargeIcon(bitmap)
                .setContentTitle(title)
                .setContentText(artist)
    }

}