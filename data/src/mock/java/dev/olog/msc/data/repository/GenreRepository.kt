package dev.olog.msc.data.repository

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.track.Genre
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.GenreGateway
import dev.olog.msc.core.gateway.SongGateway
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

internal class GenreRepository @Inject constructor(
        private val songGateway: SongGateway
) : GenreGateway {



    override fun getAll(): Observable<List<Genre>> = Observable.just(
        listOf(
            Genre(1, "genre", 0, "")
        )
    )

    override fun getAllNewRequest(): Observable<List<Genre>> {
        return getAll()
    }

    override fun getByParam(param: Long): Observable<Genre> {
        return getAll().map { it.first() }
    }

    override fun observeSongListByParam(genreId: Long): Observable<List<Song>> {
        return songGateway.getAll()
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun getMostPlayed(mediaId: MediaId): Observable<List<Song>> {
        return songGateway.getAll()
    }

    override fun insertMostPlayed(mediaId: MediaId): Completable {
        return Completable.complete()
    }

}