package dev.olog.msc.core.interactor

import dev.olog.msc.core.entity.PlaylistType
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.core.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class InsertCustomTrackListToPlaylist @Inject constructor(
    scheduler: IoScheduler,
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway

): CompletableUseCaseWithParam<InsertCustomTrackListRequest>(scheduler) {

    override fun buildUseCaseObservable(param: InsertCustomTrackListRequest): Completable {
        if (param.type == PlaylistType.PODCAST){
            return podcastPlaylistGateway.createPlaylist(param.playlistTitle)
                    .flatMapCompletable { podcastPlaylistGateway.addSongsToPlaylist(it, param.tracksId) }
        }

        return playlistGateway.createPlaylist(param.playlistTitle)
                .flatMapCompletable { playlistGateway.addSongsToPlaylist(it, param.tracksId) }
    }
}

data class InsertCustomTrackListRequest(
        val playlistTitle: String,
        val tracksId: List<Long>,
        val type: PlaylistType
)