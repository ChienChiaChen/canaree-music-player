package dev.olog.msc.musicservice

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.WidgetClasses
import dev.olog.msc.core.coroutines.CustomScope
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.core.gateway.PlayingQueueGateway
import dev.olog.msc.musicservice.model.MediaEntity
import dev.olog.msc.shared.WidgetConstants
import dev.olog.msc.shared.extensions.getAppWidgetsIdsFor
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

internal class MediaSessionQueue @Inject constructor(
    @ApplicationContext private val context: Context,
    @ServiceLifecycle lifecycle: Lifecycle,
    mediaSession: MediaSessionCompat,
    private val playerState: PlayerState,
    private val playingQueueGateway: PlayingQueueGateway,
    private val widgetClasses: WidgetClasses

) : DefaultLifecycleObserver, CoroutineScope by CustomScope() {

    private val channel = Channel<MediaSessionQueueModel<MediaEntity>>(Channel.CONFLATED)

    init {
        lifecycle.addObserver(this)

        launch {
            for (item in channel) {
                if (!item.immediate) {
                    delay(500)
                }
                yield()
                persistMiniQueue(item.queue)
                val queueItemList = item.queue.map { it.toQueueItem() }

                withContext(Dispatchers.Main) {
                    notifyWidgets()
                    mediaSession.setQueue(queueItemList)
                    playerState.updateActiveQueueId(item.activeId)
                }
            }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        cancel()
    }

    fun onNext(list: MediaSessionQueueModel<MediaEntity>) = launch {
        channel.send(list)
    }

    private suspend fun persistMiniQueue(tracks: List<MediaEntity>) {
        playingQueueGateway.updateMiniQueue(tracks.map { it.idInPlaylist to it.id })
    }

    private fun notifyWidgets() {
        for (clazz in widgetClasses.get()) {
            val ids = context.getAppWidgetsIdsFor(clazz)
            val intent = Intent(context, clazz).apply {
                action = WidgetConstants.QUEUE_CHANGED
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            }
            context.sendBroadcast(intent)
        }
    }

    private fun MediaEntity.toQueueItem(): MediaSessionCompat.QueueItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(MediaId.songId(this.id).toString())
            .setTitle(this.title)
            .setSubtitle(this.artist)
//            .setMediaUri(Uri.parse(this.image)) TODO
            .build()

        return MediaSessionCompat.QueueItem(description, this.idInPlaylist.toLong())
    }

}

data class MediaSessionQueueModel<T>(
    val activeId: Long,
    val queue: List<T>,
    val immediate: Boolean
)