package dev.olog.msc.domain.gateway

import dev.olog.msc.core.entity.Playlist
import dev.olog.msc.core.gateway.BaseGateway
import dev.olog.msc.core.gateway.ChildsHasSongs
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface PlaylistGateway :
        BaseGateway<Playlist, Long>,
    ChildsHasSongs<Long>,
        HasMostPlayed {

    fun getAllAutoPlaylists() : Observable<List<Playlist>>

    fun createPlaylist(playlistName: String): Single<Long>

    fun renamePlaylist(playlistId: Long, newTitle: String): Completable

    fun deletePlaylist(playlistId: Long): Completable

    fun clearPlaylist(playlistId: Long): Completable

    fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>): Completable

    fun getPlaylistsBlocking(): List<Playlist>

    fun insertSongToHistory(songId: Long): Completable

    fun moveItem(playlistId: Long, from: Int, to: Int): Boolean

    fun removeFromPlaylist(playlistId: Long, idInPlaylist: Long): Completable

    fun removeDuplicated(playlistId: Long): Completable

}