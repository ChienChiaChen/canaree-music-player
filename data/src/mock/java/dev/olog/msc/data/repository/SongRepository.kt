package dev.olog.msc.data.repository

import android.annotation.SuppressLint
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.SongGateway
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

internal class SongRepository @Inject constructor() : SongGateway {



    override fun getAll(): Observable<List<Song>> = Observable.just(listOf(
        Song(0, 1, 2, "title", "artist", "", "album", "", 0, 0,
            "", "", 0, 0)
    ))

    override fun getAllNewRequest(): Observable<List<Song>> {
        return getAll()
    }

    override fun getByParam(param: Long): Observable<Song> {
        return getAll().map { it.first() }
    }

    override fun getByAlbumId(albumId: Long): Observable<Song> {
        return getByParam(albumId)
    }

    @SuppressLint("Recycle")
    override fun getByUri(uri: String): Single<Song> {
        return getByParam(0).singleOrError()
    }

    override fun getUneditedByParam(songId: Long): Observable<Song> {
        return getByParam(songId)
    }

    override fun getAllUnfiltered(): Observable<List<Song>> {
        return getAll()
    }

    override fun deleteSingle(songId: Long): Completable {
        return Completable.complete()

    }

    override fun deleteGroup(songList: List<Song>): Completable {
        return Completable.complete()
    }

}

