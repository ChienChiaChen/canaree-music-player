package dev.olog.msc.musicservice

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.WidgetClasses
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.core.gateway.PlayingQueueGateway
import dev.olog.msc.musicservice.model.MediaEntity
import dev.olog.msc.shared.TrackUtils
import dev.olog.msc.shared.WidgetConstants
import dev.olog.msc.shared.extensions.getAppWidgetsIdsFor
import dev.olog.msc.shared.extensions.unsubscribe
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class MediaSessionQueue @Inject constructor(
        @ApplicationContext private val context: Context,
        @ServiceLifecycle lifecycle: Lifecycle,
        mediaSession: MediaSessionCompat,
        private val playerState: PlayerState,
        private val playingQueueGateway: PlayingQueueGateway,
        private val widgetClasses: WidgetClasses

) : DefaultLifecycleObserver {

    private val publisher : PublishSubject<MediaSessionQueueModel<MediaEntity>> = PublishSubject.create()
    private val immediatePublisher : PublishSubject<MediaSessionQueueModel<MediaEntity>> = PublishSubject.create()
    private var miniQueueDisposable : Disposable? = null
    private var immediateMiniQueueDisposable : Disposable? = null

    init {
        lifecycle.addObserver(this)
        // TODO

        miniQueueDisposable = publisher
                .toSerialized()
                .observeOn(Schedulers.computation())
                .distinctUntilChanged()
                .debounce(1, TimeUnit.SECONDS)
                .doOnNext { runBlocking { persistMiniQueue(it.queue) } }
                .map { it.toQueueItem() }
                .subscribe({ (id, queue) ->
                    mediaSession.setQueue(queue)
                    playerState.updateActiveQueueId(id)
                }, Throwable::printStackTrace)

        immediateMiniQueueDisposable = immediatePublisher
                .toSerialized()
                .observeOn(Schedulers.computation())
                .distinctUntilChanged()
            .doOnNext { runBlocking { persistMiniQueue(it.queue) } }
                .map { it.toQueueItem() }
                .subscribe({ (id, queue) ->
                    mediaSession.setQueue(queue)
                    playerState.updateActiveQueueId(id)
                }, Throwable::printStackTrace)
    }

    fun onNext(list: MediaSessionQueueModel<MediaEntity>){
        publisher.onNext(list)
    }

    fun onNextImmediate(list: MediaSessionQueueModel<MediaEntity>){
        immediatePublisher.onNext(list)
    }

    private suspend fun persistMiniQueue(tracks: List<MediaEntity>){
        playingQueueGateway.updateMiniQueue(tracks.map { it.idInPlaylist to it.id })
        notifyWidgets()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        miniQueueDisposable.unsubscribe()
        immediateMiniQueueDisposable.unsubscribe()
    }

    private fun notifyWidgets(){
        for (clazz in widgetClasses.get()) {
            val ids = context.getAppWidgetsIdsFor(clazz)
            val intent = Intent(context, clazz).apply {
                action = WidgetConstants.QUEUE_CHANGED
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            }
            context.sendBroadcast(intent)
        }
    }

    private fun MediaEntity.toQueueItem() : MediaSessionCompat.QueueItem {
        val description = MediaDescriptionCompat.Builder()
                .setMediaId(MediaId.songId(this.id).toString())
                .setTitle(this.title)
                .setSubtitle(TrackUtils.adjustArtist(this.artist))
                .setMediaUri(Uri.parse(this.image))
                .build()

        return MediaSessionCompat.QueueItem(description, this.idInPlaylist.toLong())
    }

    private fun MediaSessionQueueModel<MediaEntity>.toQueueItem(): MediaSessionQueueModel<MediaSessionCompat.QueueItem> {
        val queue = this.queue.map { it.toQueueItem() }
        return MediaSessionQueueModel(this.activeId, queue)
    }

}

data class MediaSessionQueueModel<T>(
        val activeId: Long,
        val queue: List<T>
)