package dev.olog.msc.domain.interactor.dialog

import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.core.gateway.PlaylistGateway
import dev.olog.msc.core.gateway.PodcastPlaylistGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import dev.olog.msc.core.MediaId
import io.reactivex.Completable
import javax.inject.Inject

class RenameUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val playlistGateway: PlaylistGateway,
        private val podcastPlaylistGateway: PodcastPlaylistGateway

) : CompletableUseCaseWithParam<Pair<MediaId, String>>(scheduler) {


    override fun buildUseCaseObservable(param: Pair<MediaId, String>): Completable {
        val (mediaId, newTitle) = param
        return when {
            mediaId.isPodcastPlaylist -> podcastPlaylistGateway.renamePlaylist(mediaId.categoryValue.toLong(), newTitle)
            mediaId.isPlaylist -> playlistGateway.renamePlaylist(mediaId.categoryValue.toLong(), newTitle)
            else -> Completable.error(IllegalArgumentException("not a folder nor a playlist, $mediaId"))
        }
    }
}