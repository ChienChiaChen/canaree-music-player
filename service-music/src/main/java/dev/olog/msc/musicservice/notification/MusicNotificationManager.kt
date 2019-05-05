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
import dev.olog.msc.shared.extensions.unsubscribe
import dev.olog.msc.shared.utils.isOreo
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
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

) : DefaultLifecycleObserver {

    private var isForeground: Boolean = false

    private var stopServiceAfterDelayDisposable: Disposable? = null
    private val subscriptions = CompositeDisposable()

    private val publisher = BehaviorSubject.create<Any>()
    private val currentState = MusicNotificationState()
    private var publishDisposable : Disposable? = null

    private val playerListener = object : PlayerLifecycle.Listener {
        override fun onPrepare(entity: MediaEntity) {
            onNextMetadata(entity)
        }

        override fun onMetadataChanged(entity: MediaEntity) {
            onNextMetadata(entity)
        }

        override fun onStateChanged(state: PlaybackStateCompat) {
            onNextState(state)
        }
    }

    init {
        lifecycle.addObserver(this)
        playerLifecycle.addListener(playerListener)

        publisher.toSerialized()
                .observeOn(Schedulers.computation())
                .filter {
                    when (it){
                        is MediaEntity -> currentState.isDifferentMetadata(it)
                        is PlaybackStateCompat -> currentState.isDifferentState(it)
                        is Boolean -> currentState.isDifferentFavorite(it)
                        else -> false
                    }
                }
                .subscribe({
                    publishDisposable.unsubscribe()

                    when (it){
                        is MediaEntity -> {
                            if (currentState.updateMetadata(it)) {
                                publishNotification(350)
                            }
                        }
                        is PlaybackStateCompat -> {
                            val state = currentState.updateState(it)
                            if (state){
                                publishNotification(100)
                            }
                        }
                        is Boolean -> {
                            if (currentState.updateFavorite(it)){
                                publishNotification(100)
                            }
                        }
                    }

                }, Throwable::printStackTrace)
                .addTo(subscriptions)

        observeFavoriteUseCase.execute()
                .map { it == FavoriteEnum.FAVORITE || it == FavoriteEnum.ANIMATE_TO_FAVORITE }
                .distinctUntilChanged()
                .subscribe(this::onNextFavorite, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

    private fun publishNotification(delay: Long){
        if (!isForeground && isOreo()){
            // oreo needs to post notification immediately after calling startForegroundService
            issueNotification()
        } else {
            // post delayed
            publishDisposable = Single.timer(delay, TimeUnit.MILLISECONDS)
                    .subscribe({ issueNotification() }, Throwable::printStackTrace)
        }
    }

    private fun issueNotification() {
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
        stopServiceAfterDelayDisposable.unsubscribe()
        publishDisposable.unsubscribe()
        subscriptions.clear()
    }

    private fun onNextMetadata(metadata: MediaEntity) {
        publisher.onNext(metadata)
    }

    private fun onNextState(playbackState: PlaybackStateCompat) {
        publisher.onNext(playbackState)
    }

    private fun onNextFavorite(isFavorite: Boolean){
        publisher.onNext(isFavorite)
    }

    private fun stopForeground() {
        if (!isForeground) {
            return
        }

        service.stopForeground(true)
        notificationImpl.cancel()

        isForeground = false
    }

    private fun pauseForeground() {
        if (!isForeground) {
            return
        }

        // state paused
        service.stopForeground(false)

        stopServiceAfterDelayDisposable.unsubscribe()

        stopServiceAfterDelayDisposable = Single
                .timer(MINUTES_TO_DESTROY, TimeUnit.MINUTES)
                .subscribe({ audioManager.get().dispatchEvent(KEYCODE_MEDIA_STOP) },
                        Throwable::printStackTrace)

        isForeground = false
    }

    private fun startForeground(notification: Notification) {
        if (isForeground) {
            return
        }

        service.startForeground(INotification.NOTIFICATION_ID, notification)

        // restart countdown
        stopServiceAfterDelayDisposable.unsubscribe()

        isForeground = true
    }

}
