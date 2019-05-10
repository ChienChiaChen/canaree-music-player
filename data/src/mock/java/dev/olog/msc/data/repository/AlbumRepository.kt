package dev.olog.msc.data.repository

import dev.olog.msc.core.entity.track.Album
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.AlbumGateway
import dev.olog.msc.core.gateway.SongGateway
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

internal class AlbumRepository @Inject constructor(
    private val songGateway: SongGateway
) : AlbumGateway {

    private val data = (0 until 10)
        .map { it.toLong() }
        .map { Album(it, it + 10, "album $it", "artist $it", "",
            "", 0, false) }

    override fun getAll(): Observable<List<Album>> {
        return Observable.just(data)
    }

    override fun getAllNewRequest(): Observable<List<Album>> = getAll()

    override fun getByParam(param: Long): Observable<Album> {
        return getAll().map { it.first() }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(albumId: Long): Observable<List<Song>> {
        return songGateway.getAll()
    }

    override fun observeByArtist(artistId: Long): Observable<List<Album>> {
        return getAll().map { it.filter { it.artistId == artistId } }
    }

    override fun getLastPlayed(): Observable<List<Album>> {
        return getAll()
    }

    override fun addLastPlayed(id: Long): Completable {
        return Completable.complete()
    }
}