package dev.olog.msc.data.repository

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.track.Playlist
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.PlaylistGateway
import dev.olog.msc.core.gateway.SongGateway
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

internal class PlaylistRepository @Inject constructor(
    private val songGateway: SongGateway

) : PlaylistGateway {

    private val data = listOf(
        Playlist(0, "playlist", 0, "")
    )

    override fun getAll(): Observable<List<Playlist>> {
        return Observable.just(data)
    }

    override fun getAllNewRequest(): Observable<List<Playlist>> {
        return getAll()
    }

    override fun getByParam(param: Long): Observable<Playlist> {
        return getAll().map { it.first() }
    }


    override fun getAllAutoPlaylists(): Observable<List<Playlist>> {
        return getAll()
    }

    override fun insertSongToHistory(songId: Long): Completable {
        return Completable.complete()
    }

    override fun getPlaylistsBlocking(): List<Playlist> {
        return data
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(playlistId: Long): Observable<List<Song>> {
        return songGateway.getAll()
    }

    override fun getMostPlayed(mediaId: MediaId): Observable<List<Song>> {
        return songGateway.getAll()
    }

    override fun insertMostPlayed(mediaId: MediaId): Completable {
        return Completable.complete()
    }

    override fun deletePlaylist(playlistId: Long): Completable {
        return Completable.complete()
    }

    override fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>): Completable {
        return Completable.complete()
    }


    override fun clearPlaylist(playlistId: Long): Completable {
        return Completable.complete()
    }

    override fun removeFromPlaylist(playlistId: Long, idInPlaylist: Long): Completable {
        return Completable.complete()
    }

    override fun createPlaylist(playlistName: String): Single<Long> {
        return Single.just(0)
    }

    override fun renamePlaylist(playlistId: Long, newTitle: String): Completable {
        return Completable.complete()
    }

    override fun moveItem(playlistId: Long, from: Int, to: Int): Boolean {
        return false
    }

    override fun removeDuplicated(playlistId: Long): Completable {
        return Completable.complete()
    }
}