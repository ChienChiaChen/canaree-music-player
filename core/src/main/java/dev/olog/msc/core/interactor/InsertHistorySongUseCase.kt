package dev.olog.msc.core.interactor

import dev.olog.msc.core.executors.ComputationDispatcher
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.core.interactor.base.CompletableFlowWithParam
import javax.inject.Inject

class InsertHistorySongUseCase @Inject constructor(
    schedulers: ComputationDispatcher,
    private val playlistGateway: PlaylistGateway,
    private val podcastGateway: PodcastPlaylistGateway

) : CompletableFlowWithParam<InsertHistorySongUseCase.Input>(schedulers) {

    override suspend fun buildUseCaseObservable(param: Input) {
        if (param.isPodcast) {
            podcastGateway.insertPodcastToHistory(param.id)
        } else {
            playlistGateway.insertSongToHistory(param.id)
        }

    }

    class Input(
        val id: Long,
        val isPodcast: Boolean
    )

}