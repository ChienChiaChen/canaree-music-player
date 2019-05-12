package dev.olog.msc.offlinelyrics.domain

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.OfflineLyricsGateway
import dev.olog.msc.core.interactor.base.ObservableUseCaseWithParam
import dev.olog.msc.core.interactor.item.GetSongUseCase
import io.reactivex.Observable
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

class ObserveOfflineLyricsUseCase @Inject constructor(
    executors: IoScheduler,
    private val getSongUseCase: GetSongUseCase,
    private val gateway: OfflineLyricsGateway,
    private val lyricsFromMetadata: ILyricsFromMetadata

) : ObservableUseCaseWithParam<String, Long>(executors) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(id: Long): Observable<String> = runBlocking {
        gateway.observeLyrics(id)
            .switchMap { lyrics ->
                runBlocking { getSongUseCase.execute(MediaId.songId(id)).asObservable() }
                    .map { lyricsFromMetadata.getLyrics(it) }
                    .onErrorReturnItem(lyrics)
            }
    }

}