package dev.olog.msc.musicservice.notification

import android.support.v4.media.session.PlaybackStateCompat
import dev.olog.msc.musicservice.model.MediaEntity
import java.util.concurrent.TimeUnit

internal data class MusicNotificationState (
        var id: Long = -1,
        var title: String = "",
        var artist: String = "",
        var album: String = "",
        var image: String = "",
        var isPlaying: Boolean = false,
        var bookmark: Long = -1,
        var duration: Long = -1,
        var isFavorite: Boolean = false
) {

    private fun isValidState(): Boolean{
        return id != -1L &&
                title.isNotBlank() &&
                artist.isNotBlank() &&
                album.isNotBlank() &&
//                image.isNotBlank() &&
                bookmark != -1L &&
                duration != -1L
    }

    internal fun updateMetadata(metadata: MediaEntity): Boolean {
        this.id = metadata.id
        this.title = metadata.title
        this.artist = metadata.artist
        this.album = metadata.album
        this.image = metadata.image
        this.duration = metadata.duration
        return isValidState()
    }

    internal fun updateState(state: PlaybackStateCompat): Boolean {
        this.isPlaying = state.state == PlaybackStateCompat.STATE_PLAYING
        this.bookmark = state.position
        return isValidState()
    }

    internal fun updateFavorite(isFavorite: Boolean): Boolean {
        this.isFavorite = isFavorite
        return isValidState()
    }

    internal fun isDifferentMetadata(metadata: MediaEntity): Boolean {
        return this.id != metadata.id ||
                this.title != metadata.title ||
                this.artist != metadata.artist ||
                this.album != metadata.album ||
                this.image != metadata.image
    }

    internal fun isDifferentState(state: PlaybackStateCompat): Boolean{
        val isPlaying = state.state == PlaybackStateCompat.STATE_PLAYING
        val bookmark = TimeUnit.MILLISECONDS.toSeconds(state.position)
        return this.isPlaying != isPlaying ||
                TimeUnit.MILLISECONDS.toSeconds(this.bookmark) != bookmark
    }

    internal fun isDifferentFavorite(isFavorite: Boolean): Boolean {
        return this.isFavorite != isFavorite
    }

}