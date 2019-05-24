package dev.olog.msc.floatingwindowservice.music.service

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat

internal class FloatingMusicCallback(
    private val binder: MusicServiceBinder

) : MediaControllerCompat.Callback() {

    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        binder.metadataPublisher.postValue(metadata)
    }

    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
        binder.statePublisher.postValue(state)
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        binder.repeatModePublisher.postValue(repeatMode)
    }

    override fun onShuffleModeChanged(shuffleMode: Int) {
        binder.shuffleModePublisher.postValue(shuffleMode)
    }

    override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
        binder.queuePublisher.postValue(queue)
    }

}