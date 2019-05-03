package dev.olog.msc.domain.interactor.playlist

import dev.olog.msc.core.entity.PlaylistType
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.core.gateway.PlaylistGateway
import dev.olog.msc.core.gateway.PodcastPlaylistGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
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