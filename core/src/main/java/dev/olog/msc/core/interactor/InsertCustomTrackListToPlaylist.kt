package dev.olog.msc.core.interactor

import dev.olog.msc.core.coroutines.CompletableFlowWithParam
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.entity.PlaylistType
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.msc.core.gateway.track.PlaylistGateway
import javax.inject.Inject

class InsertCustomTrackListToPlaylist @Inject constructor(
    scheduler: IoDispatcher,
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway

) : CompletableFlowWithParam<InsertCustomTrackListRequest>(scheduler) {

    override suspend fun buildUseCaseObservable(param: InsertCustomTrackListRequest) {
        if (param.type == PlaylistType.PODCAST) {
            val playlistId = podcastPlaylistGateway.createPlaylist(param.playlistTitle)
            podcastPlaylistGateway.addSongsToPlaylist(playlistId, param.tracksId)
        } else {
            val playlistId = playlistGateway.createPlaylist(param.playlistTitle)
            playlistGateway.addSongsToPlaylist(playlistId, param.tracksId)
        }


    }
}

data class InsertCustomTrackListRequest(
    val playlistTitle: String,
    val tracksId: List<Long>,
    val type: PlaylistType
)