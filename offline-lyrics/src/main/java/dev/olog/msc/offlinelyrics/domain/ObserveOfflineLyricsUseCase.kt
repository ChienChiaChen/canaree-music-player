package dev.olog.msc.offlinelyrics.domain

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.executors.ComputationDispatcher
import dev.olog.msc.core.gateway.OfflineLyricsGateway
import dev.olog.msc.core.interactor.base.ObservableFlowWithParam
import dev.olog.msc.core.interactor.item.GetSongUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveOfflineLyricsUseCase @Inject constructor(
    executors: ComputationDispatcher,
    private val getSongUseCase: GetSongUseCase,
    private val gateway: OfflineLyricsGateway,
    private val lyricsFromMetadata: ILyricsFromMetadata

) : ObservableFlowWithParam<String, Long>(executors) {

    override suspend fun buildUseCaseObservable(id: Long): Flow<String> {
        return gateway.observeLyrics(id)
            .map {
                var lyrics = it
                getSongUseCase.execute(MediaId.songId(id)).getItem()?.let { song ->
                    val metadataLyrics = lyricsFromMetadata.getLyrics(song)
                    if (metadataLyrics.isNotBlank()) {
                        lyrics = metadataLyrics
                    }
                }
                lyrics
            }
    }

}