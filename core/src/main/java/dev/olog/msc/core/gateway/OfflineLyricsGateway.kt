package dev.olog.msc.core.gateway

import dev.olog.msc.core.entity.OfflineLyrics
import io.reactivex.Completable
import io.reactivex.Observable

interface OfflineLyricsGateway {

    fun observeLyrics(id: Long): Observable<String>
    fun saveLyrics(offlineLyrics: OfflineLyrics): Completable

}