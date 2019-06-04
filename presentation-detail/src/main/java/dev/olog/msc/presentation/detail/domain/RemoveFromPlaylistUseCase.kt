package dev.olog.msc.presentation.detail.domain

import dev.olog.msc.core.entity.PlaylistType
import dev.olog.msc.core.executors.ComputationDispatcher
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.core.interactor.base.CompletableFlowWithParam
import javax.inject.Inject

class RemoveFromPlaylistUseCase @Inject constructor(
    scheduler: ComputationDispatcher,
    private val playlistGateway: PlaylistGateway,
    private val podcastGateway: PodcastPlaylistGateway

) : CompletableFlowWithParam<RemoveFromPlaylistUseCase.Input>(scheduler) {

    override suspend fun buildUseCaseObservable(input: Input) {
        if (input.type == PlaylistType.PODCAST) {
            return podcastGateway.removeSongFromPlaylist(input.playlistId, input.idInPlaylist)
        }
        return playlistGateway.removeFromPlaylist(input.playlistId, input.idInPlaylist)
    }

    class Input(
        val playlistId: Long,
        val idInPlaylist: Long,
        val type: PlaylistType
    )

}