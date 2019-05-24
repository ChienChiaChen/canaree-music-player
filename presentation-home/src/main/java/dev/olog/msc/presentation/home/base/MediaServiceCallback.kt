package dev.olog.msc.presentation.home.base

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat

class MediaServiceCallback(
    private val activity: MusicGlueActivity

) : MediaControllerCompat.Callback() {

    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        activity.metadataPublisher.value = metadata
    }

    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
        activity.statePublisher.postValue(state)
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        activity.repeatModePublisher.value = repeatMode
    }

    override fun onShuffleModeChanged(shuffleMode: Int) {
        activity.shuffleModePublisher.value = shuffleMode
    }

    override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
        activity.queuePublisher.value = queue
    }

    override fun onQueueTitleChanged(title: CharSequence?) {
        activity.queueTitlePublisher.value = title.toString()
    }

    override fun onExtrasChanged(extras: Bundle?) {
        activity.extrasPublisher.value = extras
    }

}