package dev.olog.msc.data.repository.lyrics

import dev.olog.msc.data.dao.AppDatabase
import dev.olog.msc.data.entity.OfflineLyricsEntity
import dev.olog.msc.core.entity.OfflineLyrics
import dev.olog.msc.core.gateway.OfflineLyricsGateway
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

class OfflineLyricsRepository @Inject constructor(
        appDatabase: AppDatabase

) : OfflineLyricsGateway {

    private val dao = appDatabase.offlineLyricsDao()

    override fun observeLyrics(id: Long): Observable<String> {
        return dao.observeLyrics(id).toObservable().map {
            if (it.isEmpty()) ""
            else it[0].lyrics
        }
    }

    override fun saveLyrics(offlineLyrics: OfflineLyrics): Completable {
        return Completable.fromCallable {
            dao.saveLyrics(OfflineLyricsEntity(offlineLyrics.trackId, offlineLyrics.lyrics))
        }
    }
}