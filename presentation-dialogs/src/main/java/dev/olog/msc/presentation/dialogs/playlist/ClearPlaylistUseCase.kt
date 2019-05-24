package dev.olog.msc.presentation.dialogs.playlist

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.executors.ComputationDispatcher
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.core.interactor.base.CompletableFlowWithParam
import javax.inject.Inject

class ClearPlaylistUseCase @Inject constructor(
    scheduler: ComputationDispatcher,
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway

) : CompletableFlowWithParam<MediaId>(scheduler) {

    override suspend fun buildUseCaseObservable(mediaId: MediaId) {
        val playlistId = mediaId.resolveId
        if (mediaId.isPodcastPlaylist) {
            return podcastPlaylistGateway.clearPlaylist(playlistId)
        }
        return playlistGateway.clearPlaylist(playlistId)
    }
}