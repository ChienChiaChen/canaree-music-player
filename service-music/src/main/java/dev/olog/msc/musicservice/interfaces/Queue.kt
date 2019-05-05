package dev.olog.msc.musicservice.interfaces

import android.net.Uri
import android.os.Bundle
import dev.olog.msc.core.MediaId
import dev.olog.msc.musicservice.model.PlayerMediaEntity
import dev.olog.msc.musicservice.model.PositionInQueue
import io.reactivex.Single

internal interface Queue {

    fun isReady() : Boolean

    fun getCurrentPositionInQueue(): PositionInQueue

    fun prepare(): Single<PlayerMediaEntity>

    fun handleSkipToNext(trackEnded: Boolean): PlayerMediaEntity?

    fun handleSkipToPrevious(playerBookmark: Long): PlayerMediaEntity?

    fun handlePlayFromMediaId(mediaId: MediaId, extras: Bundle?): Single<PlayerMediaEntity>

    fun handlePlayRecentlyPlayed(mediaId: MediaId): Single<PlayerMediaEntity>

    fun handlePlayMostPlayed(mediaId: MediaId): Single<PlayerMediaEntity>

    fun handleSkipToQueueItem(idInPlaylist: Long): PlayerMediaEntity

    fun handlePlayShuffle(mediaId: MediaId): Single<PlayerMediaEntity>

    fun handlePlayFolderTree(mediaId: MediaId): Single<PlayerMediaEntity>

    fun handlePlayFromGoogleSearch(query: String, extras: Bundle): Single<PlayerMediaEntity>

    fun handlePlayFromUri(uri: Uri): Single<PlayerMediaEntity>

    fun getPlayingSong(): PlayerMediaEntity

    fun handleSwap(extras: Bundle)
    fun handleSwapRelative(extras: Bundle)

    fun handleRemove(extras: Bundle): Boolean
    fun handleRemoveRelative(extras: Bundle): Boolean

    fun sort()

    fun shuffle()

    fun onRepeatModeChanged()

    fun playLater(songIds: List<Long>, isPodcast: Boolean) : PositionInQueue

    fun playNext(songIds: List<Long>, isPodcast: Boolean) : PositionInQueue
//    fun moveToPlayNext(idInPlaylist: Int) : PositionInQueue

    fun updatePodcastPosition(position: Long)

}
