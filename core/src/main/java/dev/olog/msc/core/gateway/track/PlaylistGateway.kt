package dev.olog.msc.core.gateway.track

import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.entity.track.Playlist
import dev.olog.msc.core.gateway.base.*
import io.reactivex.Completable
import io.reactivex.Single

interface PlaylistGateway :
    BaseGateway<Playlist, Long>,
    ChildsHasSongs<Long>,
    HasMostPlayed,
    HasSiblings<Playlist>,
    HasRelatedArtists<Artist>,
    PlaylistGatewayHelper {

    companion object {
        const val LAST_ADDED_ID: Long = -3000
        const val FAVORITE_LIST_ID: Long = -30012
        const val HISTORY_LIST_ID: Long = -30018

        private val autoPlaylists = listOf(
            LAST_ADDED_ID,
            FAVORITE_LIST_ID,
            HISTORY_LIST_ID
        )

        fun isAutoPlaylist(id: Long) = autoPlaylists.contains(id)
    }

    fun getAllAutoPlaylists(): List<Playlist>
    fun getPlaylistsBlocking(): List<Playlist>

}

interface PlaylistGatewayHelper {

    fun createPlaylist(playlistName: String): Single<Long>

    fun renamePlaylist(playlistId: Long, newTitle: String): Completable

    fun deletePlaylist(playlistId: Long): Completable

    fun clearPlaylist(playlistId: Long): Completable

    fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>): Completable

    fun insertSongToHistory(songId: Long): Completable

    fun moveItem(playlistId: Long, from: Int, to: Int): Boolean

    fun removeFromPlaylist(playlistId: Long, idInPlaylist: Long): Completable

    fun removeDuplicated(playlistId: Long): Completable

}