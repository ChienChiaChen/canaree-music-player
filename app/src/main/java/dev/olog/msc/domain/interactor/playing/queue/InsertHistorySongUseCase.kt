package dev.olog.msc.domain.interactor.playing.queue

import dev.olog.msc.core.executor.IoScheduler
import dev.olog.msc.core.gateway.PlaylistGateway
import dev.olog.msc.core.gateway.PodcastPlaylistGateway
import dev.olog.msc.core.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class InsertHistorySongUseCase @Inject constructor(
    schedulers: IoScheduler,
    private val playlistGateway: PlaylistGateway,
    private val podcastGateway: PodcastPlaylistGateway

): CompletableUseCaseWithParam<InsertHistorySongUseCase.Input>(schedulers) {

    override fun buildUseCaseObservable(param: InsertHistorySongUseCase.Input): Completable {
        if (param.isPodcast){
            return podcastGateway.insertPodcastToHistory(param.id)
        }
        return playlistGateway.insertSongToHistory(param.id)
    }

    class Input(
            val id: Long,
            val isPodcast: Boolean
    )

}