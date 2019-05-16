package dev.olog.msc.musicservice.interfaces

import android.net.Uri
import android.os.Bundle
import dev.olog.msc.core.MediaId
import dev.olog.msc.musicservice.model.PlayerMediaEntity
import dev.olog.msc.musicservice.model.PositionInQueue

internal interface Queue {

    fun getCurrentPositionInQueue(): PositionInQueue

    suspend fun prepare(): PlayerMediaEntity

    suspend fun handleSkipToNext(trackEnded: Boolean): PlayerMediaEntity?
    suspend fun handleSkipToPrevious(playerBookmark: Long): PlayerMediaEntity?
    suspend fun handleSkipToQueueItem(idInPlaylist: Long): PlayerMediaEntity

    suspend fun handlePlayFromMediaId(mediaId: MediaId, extras: Bundle?): PlayerMediaEntity
    suspend fun handlePlayRecentlyPlayed(mediaId: MediaId): PlayerMediaEntity
    suspend fun handlePlayMostPlayed(mediaId: MediaId): PlayerMediaEntity

    suspend fun handlePlayShuffle(mediaId: MediaId): PlayerMediaEntity

    suspend fun handlePlayFolderTree(mediaId: MediaId): PlayerMediaEntity

    suspend fun handlePlayFromGoogleSearch(query: String, extras: Bundle): PlayerMediaEntity?

    suspend fun handlePlayFromUri(uri: Uri): PlayerMediaEntity?

    suspend fun getPlayingSong(): PlayerMediaEntity

    suspend fun handleSwap(from: Int, to: Int, relative: Boolean)

    suspend fun handleRemove(position: Int, relative: Boolean, callback: (Boolean) -> Unit)

    suspend fun sort()

    suspend fun shuffle()

    fun onRepeatModeChanged()

    suspend fun playLater(songIds: List<Long>, isPodcast: Boolean) : PositionInQueue

    suspend fun playNext(songIds: List<Long>, isPodcast: Boolean) : PositionInQueue
//    fun moveToPlayNext(idInPlaylist: Int) : PositionInQueue

    fun updatePodcastPosition(position: Long)

}
