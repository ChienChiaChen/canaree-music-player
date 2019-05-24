package dev.olog.msc.musicservice.notification

import android.app.Notification
import android.app.Service
import android.media.AudioManager
import android.support.v4.media.session.PlaybackStateCompat
import android.view.KeyEvent.KEYCODE_MEDIA_STOP
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dagger.Lazy
import dev.olog.msc.core.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.core.dagger.scope.PerService
import dev.olog.msc.core.entity.favorite.FavoriteEnum
import dev.olog.msc.core.interactor.favorite.ObserveFavoriteAnimationUseCase
import dev.olog.msc.musicservice.interfaces.PlayerLifecycle
import dev.olog.msc.musicservice.model.MediaEntity
import dev.olog.msc.musicservice.utils.dispatchEvent
import dev.olog.msc.shared.core.coroutines.DefaultScope
import dev.olog.msc.shared.utils.assertBackgroundThread
import dev.olog.msc.shared.utils.isOreo
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val MINUTES_TO_DESTROY = 30L

@PerService
internal class MusicNotificationManager @Inject constructor(
        private val service: Service,
        @ServiceLifecycle lifecycle: Lifecycle,
        private val audioManager: Lazy<AudioManager>,
        private val notificationImpl: INotification,
        observeFavoriteUseCase: ObserveFavoriteAnimationUseCase,
        playerLifecycle: PlayerLifecycle

) : DefaultLifecycleObserver, CoroutineScope by DefaultScope() {

    private var publishNotificationJob : Job? = null
    private var delayedStopNotificationJob : Job? = null

    private var isForeground: Boolean = false

    private val currentState = MusicNotificationState()

    private val playerListener = object : PlayerLifecycle.Listener {
        override fun onPrepare(entity: MediaEntity) {
            launch(Dispatchers.Main) {
                channel.send(entity)
            }
        }

        override fun onMetadataChanged(entity: MediaEntity) {
            launch(Dispatchers.Main) {
                if (currentState.isDifferentMetadata(entity)){
                    channel.send(entity)
                }
            }
        }

        override fun onStateChanged(state: PlaybackStateCompat) {
            launch(Dispatchers.Main) {
                if (currentState.isDifferentState(state)){
                    channel.send(state)
                }
            }
        }
    }

    private val channel = Channel<Any>()

    init {
        lifecycle.addObserver(this)
        playerLifecycle.addListener(playerListener)

        launch {
            observeFavoriteUseCase.execute()
                .map { it == FavoriteEnum.FAVORITE || it == FavoriteEnum.ANIMATE_TO_FAVORITE }
                .distinctUntilChanged()
                .filter { currentState.isDifferentFavorite(it) }
                .collect { channel.send(it) }
        }

        launch {
            for (type in channel) {

                when (type){
                    is MediaEntity -> {
                        if (currentState.updateMetadata(type)) {
                            publishNotification(350)
                        }
                    }
                    is PlaybackStateCompat -> {
                        val state = currentState.updateState(type)
                        if (state){
                            publishNotification(100)
                        }
                    }
                    is Boolean -> {
                        if (currentState.updateFavorite(type)){
                            publishNotification(100)
                        }
                    }
                }
            }
        }
    }

    private suspend fun publishNotification(delayMillis: Long){
        if (!isForeground && isOreo()){
            // oreo needs to post notification immediately after calling startForegroundService
            issueNotification()
        } else {
            publishNotificationJob?.cancel()
            publishNotificationJob = launch {
                delay(delayMillis)
                issueNotification()
            }
        }
    }

    private suspend fun issueNotification() {
        assertBackgroundThread()

        val copy = currentState.copy()
        val notification = notificationImpl.update(copy)
        if (copy.isPlaying){
            startForeground(notification)
        } else {
            pauseForeground()
        }

    }

    override fun onDestroy(owner: LifecycleOwner) {
        stopForeground()
        cancel()
        publishNotificationJob?.cancel()
        delayedStopNotificationJob?.cancel()
    }

    private fun stopForeground() {
        if (!isForeground) {
            return
        }

        service.stopForeground(true)
        notificationImpl.cancel()

        isForeground = false
    }

    private suspend fun pauseForeground() {
        if (!isForeground) {
            return
        }

        // state paused
        service.stopForeground(false)

        delayedStopNotificationJob?.cancel()
        delayedStopNotificationJob = launch {
            delay(TimeUnit.MINUTES.toMillis(MINUTES_TO_DESTROY))
            withContext(Dispatchers.Main){
                audioManager.get().dispatchEvent(KEYCODE_MEDIA_STOP)
            }
        }

        isForeground = false
    }

    private suspend fun startForeground(notification: Notification) {
        if (isForeground) {
            return
        }
        withContext(Dispatchers.Main){
            service.startForeground(INotification.NOTIFICATION_ID, notification)
        }

        // restart countdown
        delayedStopNotificationJob?.cancel()

        isForeground = true
    }

}
