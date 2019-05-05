package dev.olog.msc.offlinelyrics.domain

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.OfflineLyricsGateway
import dev.olog.msc.core.interactor.base.ObservableUseCaseWithParam
import dev.olog.msc.core.interactor.item.GetSongUseCase
import io.reactivex.Observable
import javax.inject.Inject

class ObserveOfflineLyricsUseCase @Inject constructor(
        executors: IoScheduler,
        private val getSongUseCase: GetSongUseCase,
        private val gateway: OfflineLyricsGateway,
        private val lyricsFromMetadata: ILyricsFromMetadata

) : ObservableUseCaseWithParam<String, Long>(executors) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(id: Long): Observable<String> {
        return gateway.observeLyrics(id)
                .switchMap { lyrics -> getSongUseCase.execute(MediaId.songId(id))
                        .map { lyricsFromMetadata.getLyrics(it) }
                        .onErrorReturnItem(lyrics)
                }
    }

}