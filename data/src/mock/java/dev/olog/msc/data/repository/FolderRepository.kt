package dev.olog.msc.data.repository

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.track.Folder
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.FolderGateway
import dev.olog.msc.core.gateway.SongGateway
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

internal class FolderRepository @Inject constructor(

        private val songGateway: SongGateway

): FolderGateway {

    override fun getAll(): Observable<List<Folder>> {
        return Observable.just(listOf(
            Folder("folder", "", 0, "")
        ))
    }

    override fun getAllNewRequest(): Observable<List<Folder>> {
        return getAll()
    }

    override fun getByParam(param: String): Observable<Folder> {
        return getAll().map { it.first() }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(path: String): Observable<List<Song>> {
        return songGateway.getAll()
    }

    override fun getMostPlayed(mediaId: MediaId): Observable<List<Song>> {
        return songGateway.getAll()
    }

    override fun insertMostPlayed(mediaId: MediaId): Completable {
        return Completable.complete()
    }

    override fun getAllUnfiltered(): Observable<List<Folder>> {
        return getAll()
    }

}