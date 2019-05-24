package dev.olog.msc.musicservice

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.core.gateway.FavoriteGateway
import dev.olog.msc.musicservice.interfaces.Queue
import dev.olog.msc.musicservice.interfaces.SkipType
import dev.olog.msc.musicservice.model.PlayerMediaEntity
import dev.olog.msc.shared.MusicConstants
import dev.olog.msc.shared.core.coroutines.CustomScope
import dev.olog.msc.shared.utils.assertBackgroundThread
import dev.olog.msc.shared.utils.assertMainThread
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

internal class ActionManager @Inject constructor(
    @ServiceLifecycle lifecycle: Lifecycle,
    private val queueManager: Queue,
    private val gateway: FavoriteGateway,
    private val repeatMode: RepeatMode,
    private val shuffleMode: ShuffleMode,
    private val playerState: PlayerState

) : DefaultLifecycleObserver, CoroutineScope by CustomScope() {

    private val channel = Channel<Action>(capacity = Channel.UNLIMITED)
    lateinit var callback: Callback

    init {
        lifecycle.addObserver(this)
        launch {
            for (action in channel) {
                handleAction(action)
            }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        cancel()
    }

    fun dispatchAction(action: Action) = launch {
        channel.send(action)
    }

    private suspend fun handleAction(action: Action) = coroutineScope {
        assertBackgroundThread() // TODO remove after testing
        when (action) {
            is Action.Prepare -> handleOnPrepare()
            is Action.Resume -> withContext(Dispatchers.Main) { callback.onResume() }
            is Action.Pause -> withContext(Dispatchers.Main) { callback.onPause(stopService = action.stopService, releaseFocus = true) }
            is Action.SkipToNext -> handleSkipToNext(action)
            is Action.SkipToPrevious -> handleSkipToPrevious(action)
            is Action.SkipToQueueItem -> handleSkipToQueueitem(action)
            is Action.Seek -> withContext(Dispatchers.Main) { callback.onSeek(action.pos) }
            is Action.SetRating -> withContext(Dispatchers.IO) { gateway.toggleFavorite() }
            is Action.RepeatChanged -> handleOnRepeatModeChanged()
            is Action.ShuffleChanged -> handleOnShuffleModeChanged()
            is Action.PlayFromMediaId -> handleOnPlayFromMediaId(action)
            is Action.PlayFromSearch -> handlePlayFromSearch(action)
            is Action.PlayFromUri -> handlePlayFromUri(action)
            is Action.PlayShuffle -> handlePlayShuffle(action)
            is Action.Swap -> queueManager.handleSwap(action.from, action.to, action.relative)
            is Action.Remove -> queueManager.handleRemove(action.position, action.relative, action.callback)
            is Action.ForwardBy -> withContext(Dispatchers.Main) { callback.onForwardBy(action.seconds) }
            is Action.ReplayBy -> withContext(Dispatchers.Main) { callback.onReplayBy(action.seconds) }
        }
    }

    private suspend fun handleOnPrepare() {
        val item = queueManager.prepare()
        withContext(Dispatchers.Main) {
            assertMainThread() // TODO remove after testing
            callback.onPrepare(item)
        }
    }

    private suspend fun handleSkipToNext(action: Action.SkipToNext) {
        val trackEnded = action.trackEnded
        val metadata = queueManager.handleSkipToNext(trackEnded)

        withContext(Dispatchers.Main) {
            if (metadata != null) {
                val skipType = if (trackEnded) SkipType.TRACK_ENDED else SkipType.SKIP_NEXT
                callback.onPlayNext(metadata, skipType)
            } else {
                val currentSong = queueManager.getPlayingSong()
                callback.onPlay(currentSong)
                callback.onPause(stopService = true, releaseFocus = true)
                callback.onSeek(0L)
            }
        }

    }

    private suspend fun handleSkipToPrevious(action: Action.SkipToPrevious) {
        val item = queueManager.handleSkipToPrevious(action.bookmark)
        if (item != null) {
            withContext(Dispatchers.Main) {
                callback.onPlayNext(item, SkipType.SKIP_PREVIOUS)
            }
        }
    }

    private suspend fun handleSkipToQueueitem(action: Action.SkipToQueueItem) {
        val mediaEntity = queueManager.handleSkipToQueueItem(action.id)
        withContext(Dispatchers.Main) {
            callback.onPlay(mediaEntity)
        }
    }

    private suspend fun handleOnRepeatModeChanged() {
        this.repeatMode.update()
        playerState.toggleSkipToActions(queueManager.getCurrentPositionInQueue())
        queueManager.onRepeatModeChanged()
    }

    private suspend fun handleOnShuffleModeChanged() {
        val newShuffleMode = shuffleMode.update()
        if (newShuffleMode) {
            queueManager.shuffle()
        } else {
            queueManager.sort()
        }
        playerState.toggleSkipToActions(queueManager.getCurrentPositionInQueue())
    }

    private suspend fun handleOnPlayFromMediaId(action: Action.PlayFromMediaId) {
        val mediaId = action.mediaId
        val extras = action.extras
        var item: PlayerMediaEntity? = null
        if (extras.isEmpty ||
            extras.getString(MusicConstants.ARGUMENT_SORT_TYPE) != null ||
            extras.getString(MusicConstants.ARGUMENT_SORT_ARRANGING) != null
        ) {
            item = queueManager.handlePlayFromMediaId(mediaId, extras)
        } else if (extras.getBoolean(MusicConstants.BUNDLE_MOST_PLAYED, false)) {
            item = queueManager.handlePlayMostPlayed(mediaId)
        } else if (extras.getBoolean(MusicConstants.BUNDLE_RECENTLY_PLAYED, false)) {
            item = queueManager.handlePlayRecentlyPlayed(mediaId)
        }
        item?.let { mediaEntity ->
            withContext(Dispatchers.Main) {
                callback.onPlay(mediaEntity)
            }
        }
    }

    private suspend fun handlePlayFromSearch(action: Action.PlayFromSearch) {
        val item = queueManager.handlePlayFromGoogleSearch(action.query, action.extras)
        if (item == null){
            Log.w("ActionManager", "song not found by voice search ${action.query}, ${action.extras}")
            return
        }
        withContext(Dispatchers.Main) {
            callback.onPlay(item)
        }
    }

    private suspend fun handlePlayFromUri(action: Action.PlayFromUri) {
        // TODO check if works
        val item = queueManager.handlePlayFromUri(action.uri)
        if (item == null){
            Log.w("ActionManager", "song not found by uri ${action.uri}")
            return
        }
        withContext(Dispatchers.Main) {
            callback.onPlay(item)
        }
    }

    private suspend fun handlePlayShuffle(action: Action.PlayShuffle){
        val item = queueManager.handlePlayShuffle(action.mediaId)
        withContext(Dispatchers.Main) {
            callback.onPlay(item)
        }
    }

    interface Callback {
        fun onPrepare(playerModel: PlayerMediaEntity)
        fun onResume()
        fun onPause(stopService: Boolean, releaseFocus: Boolean)

        fun onPlayNext(playerModel: PlayerMediaEntity, skipType: SkipType)
        fun onPlay(playerModel: PlayerMediaEntity)

        fun onSeek(millis: Long)

        fun onReplayBy(seconds: Int)
        fun onForwardBy(seconds: Int)
    }

    sealed class Action {
        object Prepare : Action()
        object Resume : Action()
        class Pause(val stopService: Boolean) : Action()
        data class Seek(val pos: Long) : Action()

        class SkipToNext(val trackEnded: Boolean) : Action()
        class SkipToPrevious(val bookmark: Long) : Action()
        class SkipToQueueItem(val id: Long) : Action()

        class PlayFromMediaId(val mediaId: MediaId, val extras: Bundle) : Action()
        class PlayFromSearch(val query: String, val extras: Bundle) : Action()
        class PlayFromUri(val uri: Uri) : Action()
        class PlayShuffle(val mediaId: MediaId) : Action()

        object SetRating : Action()

        object ShuffleChanged : Action()
        object RepeatChanged : Action()

        class Swap(val from: Int, val to: Int, val relative: Boolean) : Action()
        class Remove(val position: Int, val relative: Boolean, val callback: (Boolean) -> Unit) : Action()

        class ForwardBy(val seconds: Int): Action()
        class ReplayBy(val seconds: Int): Action()
    }


}