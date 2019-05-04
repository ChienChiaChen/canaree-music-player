package dev.olog.msc.musicservice

import android.app.Service
import android.content.Intent
import android.provider.MediaStore
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.media.MediaBrowserServiceCompat
import dagger.android.AndroidInjection
import dev.olog.msc.musicservice.interfaces.Player
import dev.olog.msc.musicservice.interfaces.ServiceLifecycleController
import dev.olog.msc.shared.MusicConstants
import javax.inject.Inject

abstract class BaseMusicService : MediaBrowserServiceCompat(),
        LifecycleOwner,
        ServiceLifecycleController {

    companion object {
        private const val ACTION_KEEP_SERVICE_ALIVE = "action.KEEP_SERVICE_ALIVE"
    }

    @Suppress("LeakingThis")
    private val dispatcher = ServiceLifecycleDispatcher(this)

    @Inject lateinit var player: Player

    private var serviceStarted = false

    @CallSuper
    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    @CallSuper
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceStarted = true

        handleIntent(intent)

        return Service.START_NOT_STICKY
    }

    @CallSuper
    override fun onDestroy() {
        dispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) return

        val action = intent.action

        when (action){
            null -> stop()
            MusicConstants.ACTION_SHUFFLE -> handleAppShortcutShuffle(intent)
            MusicConstants.ACTION_PLAY -> handleAppShortcutPlay(intent)
            MusicConstants.ACTION_PLAY_PAUSE -> handlePlayPause(intent)
            MusicConstants.ACTION_SKIP_NEXT -> handleSkipNext(intent)
            MusicConstants.ACTION_SKIP_PREVIOUS -> handleSkipPrevious(intent)
            MusicConstants.ACTION_SKIP_TO_ITEM -> handleSkipToItem(intent)
            MusicConstants.ACTION_TOGGLE_FAVORITE -> handleToggleFavorite()
            MusicConstants.ACTION_PLAY_FROM_URI -> handlePlayFromUri(intent)
            PendingIntents.ACTION_STOP_SLEEP_END -> handleSleepTimerEnd(intent)
            MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH -> handlePlayFromVoiceSearch(intent)
            else -> handleMediaButton(intent)
        }
    }

    protected abstract fun handleAppShortcutPlay(intent: Intent)
    protected abstract fun handleAppShortcutShuffle(intent: Intent)
    protected abstract fun handlePlayPause(intent: Intent)
    protected abstract fun handleSkipNext(intent: Intent)
    protected abstract fun handleSkipPrevious(intent: Intent)
    protected abstract fun handleSkipToItem(intent: Intent)
    protected abstract fun handleSleepTimerEnd(intent: Intent)
    protected abstract fun handlePlayFromVoiceSearch(intent: Intent)
    protected abstract fun handleToggleFavorite()
    protected abstract fun handlePlayFromUri(intent: Intent)

    override fun start() {
        if (!serviceStarted) {
            val intent = Intent(this, MusicService::class.java)
            intent.action = ACTION_KEEP_SERVICE_ALIVE
            ContextCompat.startForegroundService(this, intent)

            serviceStarted = true
        }
    }

    override fun stop() {
        if (serviceStarted) {
            serviceStarted = false
            stopSelf()
        }
    }

    override fun getLifecycle(): Lifecycle = dispatcher.lifecycle

    protected abstract fun handleMediaButton(intent: Intent)

}