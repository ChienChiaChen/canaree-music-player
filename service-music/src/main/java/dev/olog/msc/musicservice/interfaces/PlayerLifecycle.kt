package dev.olog.msc.musicservice.interfaces

import android.support.v4.media.session.PlaybackStateCompat
import dev.olog.msc.musicservice.model.MediaEntity

internal interface PlayerLifecycle {

    fun addListener(listener: Listener)
    fun removeListener(listener: Listener)

    interface Listener {
        fun onPrepare(entity: MediaEntity) {}
        fun onMetadataChanged(entity: MediaEntity) {}
        fun onStateChanged(state: PlaybackStateCompat){}
        fun onSeek(where: Long){}
    }

}