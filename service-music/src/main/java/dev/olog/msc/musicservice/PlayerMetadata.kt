package dev.olog.msc.musicservice

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.msc.core.WidgetClasses
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.core.dagger.scope.PerService
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.imageprovider.IImageProvider
import dev.olog.msc.imageprovider.ImageModel
import dev.olog.msc.musicservice.interfaces.PlayerLifecycle
import dev.olog.msc.musicservice.model.MediaEntity
import dev.olog.msc.shared.MusicConstants
import dev.olog.msc.shared.TrackUtils
import dev.olog.msc.shared.WidgetConstants
import dev.olog.msc.shared.extensions.getAppWidgetsIdsFor
import dev.olog.msc.shared.extensions.unsubscribe
import io.reactivex.disposables.Disposable
import javax.inject.Inject

@PerService
internal class PlayerMetadata @Inject constructor(
        @ServiceLifecycle lifecycle: Lifecycle,
        @ApplicationContext private val context: Context,
        private val mediaSession: MediaSessionCompat,
        playerLifecycle: PlayerLifecycle,
        private val widgetClasses: WidgetClasses,
        appPreferencesGateway: AppPreferencesGateway,
        private val imageProvider: IImageProvider

) : PlayerLifecycle.Listener, DefaultLifecycleObserver {

    private var disposable: Disposable? = null
    private var showLockscreenImage = false

    private val builder = MediaMetadataCompat.Builder()

    init {
        lifecycle.addObserver(this)
        playerLifecycle.addListener(this)
        disposable = appPreferencesGateway.observeLockscreenArtworkEnabled()
                .subscribe({ showLockscreenImage = it }, Throwable::printStackTrace)
    }

    override fun onPrepare(entity: MediaEntity) {
        update(entity)
    }

    override fun onMetadataChanged(entity: MediaEntity) {
        update(entity)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        disposable.unsubscribe()
    }

    private fun update(entity: MediaEntity) {
        builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, entity.mediaId.toString())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, entity.title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, TrackUtils.adjustArtist(entity.artist))
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, TrackUtils.adjustAlbum(entity.album))
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, entity.title)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, entity.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, entity.album)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, entity.duration)
                .putString(MediaMetadataCompat.METADATA_KEY_ART_URI, entity.image)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, entity.image)
                .putString(MusicConstants.PATH, entity.path)
                .putLong(MusicConstants.IS_PODCAST, if (entity.isPodcast) 1 else 0)

        if (showLockscreenImage) {
            val model = ImageModel(entity.mediaId, entity.image)
            imageProvider.getBitmapAsync(context, model, action = { bitmap ->
                builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
                mediaSession.setMetadata(builder.build())
            })
        } else {
            mediaSession.setMetadata(builder.build())
        }

        notifyWidgets(entity)

    }

    private fun notifyWidgets(entity: MediaEntity){
        for (clazz in widgetClasses.get()) {
            val ids = context.getAppWidgetsIdsFor(clazz)

            val intent = Intent(context, clazz).apply {
                action = WidgetConstants.METADATA_CHANGED
                putExtra(WidgetConstants.ARGUMENT_SONG_ID, entity.id)
                putExtra(WidgetConstants.ARGUMENT_TITLE, entity.title)
                putExtra(WidgetConstants.ARGUMENT_SUBTITLE, entity.artist)
                putExtra(WidgetConstants.ARGUMENT_IMAGE, entity.image)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            }

            context.sendBroadcast(intent)
        }

    }

}
