package dev.olog.msc.presentation.dialogs.rename

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.executors.ComputationDispatcher
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.core.interactor.base.CompletableFlowWithParam
import javax.inject.Inject

class RenameUseCase @Inject constructor(
    scheduler: ComputationDispatcher,
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway

) : CompletableFlowWithParam<Pair<MediaId, String>>(scheduler) {


    override suspend fun buildUseCaseObservable(param: Pair<MediaId, String>) {
        val (mediaId, newTitle) = param
        when {
            mediaId.isPodcastPlaylist -> podcastPlaylistGateway.renamePlaylist(mediaId.categoryValue.toLong(), newTitle)
            mediaId.isPlaylist -> playlistGateway.renamePlaylist(mediaId.categoryValue.toLong(), newTitle)
            else -> throw IllegalArgumentException("not a folder nor a playlist, $mediaId")
        }
    }
}