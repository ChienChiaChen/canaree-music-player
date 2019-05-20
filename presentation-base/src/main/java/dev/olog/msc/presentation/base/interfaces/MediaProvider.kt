package dev.olog.msc.presentation.base.interfaces

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.sort.SortArranging
import dev.olog.msc.core.entity.sort.SortType

interface MediaProvider {

    fun onMetadataChanged(): LiveData<MediaMetadataCompat>
    fun onStateChanged(): LiveData<PlaybackStateCompat>
    fun onRepeatModeChanged(): LiveData<Int>
    fun onShuffleModeChanged(): LiveData<Int>
    fun onQueueChanged(): LiveData<List<MediaSessionCompat.QueueItem>>
    fun onQueueTitleChanged(): LiveData<String>
    fun onExtrasChanged(): LiveData<Bundle>

    fun playFromMediaId(mediaId: MediaId, sortType: SortType? = null, sortArranging: SortArranging? = null)
    fun playMostPlayed(mediaId: MediaId)
    fun playRecentlyAdded(mediaId: MediaId)

    fun skipToQueueItem(idInPlaylist: Long)
    fun shuffle(mediaId: MediaId)
    fun skipToNext()
    fun skipToPrevious()
    fun playPause()
    fun seekTo(where: Long)
    fun toggleShuffleMode()
    fun toggleRepeatMode()

    fun addToPlayNext(mediaId: MediaId)
    fun moveToPlayNext(mediaId: MediaId)

    fun togglePlayerFavorite()

    fun swap(from: Int, to: Int)
    fun swapRelative(from: Int, to: Int)

    fun remove(position: Int)
    fun removeRelative(position: Int)

    fun replayTenSeconds()
    fun forwardTenSeconds()

    fun replayThirtySeconds()
    fun forwardThirtySeconds()

}