package dev.olog.msc.data.repository.lyrics

import dev.olog.msc.core.entity.OfflineLyrics
import dev.olog.msc.core.gateway.OfflineLyricsGateway
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

internal class OfflineLyricsRepository @Inject constructor() : OfflineLyricsGateway {

    override fun observeLyrics(id: Long): Observable<String> {
        return Observable.just("")
    }

    override fun saveLyrics(offlineLyrics: OfflineLyrics): Completable {
        return Completable.complete()
    }
}