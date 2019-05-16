package dev.olog.msc.core.gateway.track

import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.entity.track.Playlist
import dev.olog.msc.core.gateway.base.*
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

}

interface PlaylistGatewayHelper {

    fun createPlaylist(playlistName: String): Single<Long>

    fun renamePlaylist(playlistId: Long, newTitle: String)

    fun deletePlaylist(playlistId: Long)

    suspend fun clearPlaylist(playlistId: Long)

    fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>)

    suspend fun insertSongToHistory(songId: Long)

    fun moveItem(playlistId: Long, from: Int, to: Int): Boolean

    suspend fun removeFromPlaylist(playlistId: Long, idInPlaylist: Long)

    fun removeDuplicated(playlistId: Long)

}