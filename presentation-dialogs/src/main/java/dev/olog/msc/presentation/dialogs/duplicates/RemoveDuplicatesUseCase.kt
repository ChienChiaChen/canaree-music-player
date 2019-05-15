package dev.olog.msc.presentation.dialogs.duplicates

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.core.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class RemoveDuplicatesUseCase @Inject constructor(
    scheduler: IoScheduler,
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway

): CompletableUseCaseWithParam<MediaId>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Completable {
        val playlistId = mediaId.resolveId
        if (mediaId.isPodcastPlaylist){
            return podcastPlaylistGateway.removeDuplicated(playlistId)
        }
        return playlistGateway.removeDuplicated(playlistId)
    }
}