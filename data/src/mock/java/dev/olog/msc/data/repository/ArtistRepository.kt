package dev.olog.msc.data.repository

import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.ArtistGateway
import dev.olog.msc.core.gateway.SongGateway
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

internal class ArtistRepository @Inject constructor(
        private val songGateway: SongGateway

) : ArtistGateway {

    private val data = (0 until 10)
        .map { it.toLong() }
        .map { Artist(it, "artist $it", "", 0, 0, "") }

    override fun getAll(): Observable<List<Artist>> {
        return Observable.just(data)
    }

    override fun getAllNewRequest(): Observable<List<Artist>> {
        return getAll()
    }

    override fun getByParam(param: Long): Observable<Artist> {
        return getAll().map { it.first() }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(artistId: Long): Observable<List<Song>> {
        return songGateway.getAll()
    }

    override fun getLastPlayed(): Observable<List<Artist>> {
        return getAll()
    }

    override fun addLastPlayed(id: Long): Completable {
        return Completable.complete()
    }

}