package dev.olog.msc.floatingwindowservice.music.service

import android.content.ComponentName
import android.content.Context
import android.os.RemoteException
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.*
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.core.dagger.scope.PerService
import dev.olog.msc.presentation.navigator.Services
import dev.olog.msc.shared.MusicServiceConnectionState
import dev.olog.msc.shared.core.coroutines.DefaultScope
import dev.olog.msc.shared.extensions.isPaused
import dev.olog.msc.shared.extensions.isPlaying
import dev.olog.msc.shared.ui.extensions.distinctUntilChanged
import dev.olog.msc.shared.ui.extensions.filter
import dev.olog.msc.shared.ui.extensions.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

@PerService
internal class MusicServiceBinder @Inject constructor(
    @ApplicationContext private val context: Context,
    @ServiceLifecycle lifecycle: Lifecycle

) : DefaultLifecycleObserver, CoroutineScope by DefaultScope() {

    private val mediaBrowser = MediaBrowserCompat(
        context, ComponentName(context, Services.music()),
        FloatingMusicConnection(this), null
    )
    private val publisher = BroadcastChannel<MusicServiceConnectionState>(Channel.CONFLATED)

    internal val metadataPublisher = MutableLiveData<MediaMetadataCompat>()
    internal val statePublisher = MutableLiveData<PlaybackStateCompat>()
    internal val repeatModePublisher = MutableLiveData<Int>()
    internal val shuffleModePublisher = MutableLiveData<Int>()
    internal val queuePublisher = MutableLiveData<List<MediaSessionCompat.QueueItem>>()

    private var mediaController: MediaControllerCompat? = null
    private val callback = FloatingMusicCallback(this)

    init {
        lifecycle.addObserver(this)

        launch {
            publisher.send(MusicServiceConnectionState.NONE)
            for (state in publisher.openSubscription()) {
                when (state) {
                    MusicServiceConnectionState.CONNECTED -> onConnected()
                    MusicServiceConnectionState.FAILED -> onConnectionFailed()
                    else -> {
                    }
                }
            }
        }
        mediaBrowser.connect()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        mediaController?.unregisterCallback(callback)
        cancel()
        this.mediaBrowser.disconnect()
    }

    private fun onConnected() {
        try {
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken)
            mediaController!!.registerCallback(callback)
            initialize(mediaController!!)
        } catch (e: RemoteException) {
            e.printStackTrace()
            onConnectionFailed()
        }
    }

    private fun onConnectionFailed() {
        mediaController?.unregisterCallback(callback)
    }

    internal fun updateConnectionState(state: MusicServiceConnectionState) {
        launch { publisher.send(state) }
    }

    private fun initialize(mediaController: MediaControllerCompat) {
        callback.onMetadataChanged(mediaController.metadata)
        callback.onPlaybackStateChanged(mediaController.playbackState)
        callback.onRepeatModeChanged(mediaController.repeatMode)
        callback.onShuffleModeChanged(mediaController.shuffleMode)
        callback.onQueueChanged(mediaController.queue)
    }

    fun onStateChanged(): LiveData<PlaybackStateCompat> {
        return statePublisher
    }

    fun next() {
        mediaController?.transportControls?.skipToNext()
    }

    fun previous() {
        mediaController?.transportControls?.skipToPrevious()
    }

    fun playPause() {
        val playbackState = mediaController?.playbackState
        playbackState?.let {
            if (it.state == PlaybackStateCompat.STATE_PLAYING) {
                mediaController?.transportControls?.pause()
            } else {
                mediaController?.transportControls?.play()
            }
        }
    }

    fun seekTo(progress: Long) {
        mediaController?.transportControls?.seekTo(progress)
    }

    fun skipToNext() {
        mediaController?.transportControls?.skipToNext()
    }

    fun skipToPrevious() {
        mediaController?.transportControls?.skipToPrevious()
    }

    val animatePlayPauseLiveData: LiveData<Int> = statePublisher
        .filter { it.isPlaying() || it.isPaused() }
        .map { it.state }
        .distinctUntilChanged()

    val onBookmarkChangedLiveData: LiveData<Long> = statePublisher
        .filter { it.isPlaying() || it.isPaused() }
        .map { it.position }

    val onMetadataChanged: LiveData<MusicServiceMetadata> = metadataPublisher
        .map {
            MusicServiceMetadata(
                it.getId(), it.getTitle().toString(),
                it.getArtist().toString(), it.getMediaId(),
                it.getDuration(), it.isPodcast()
            )
        }

    val onMaxChangedLiveData: LiveData<Long> = metadataPublisher
        .map { it.getDuration() }

}