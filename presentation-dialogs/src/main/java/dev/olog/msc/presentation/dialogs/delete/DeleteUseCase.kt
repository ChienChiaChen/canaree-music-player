package dev.olog.msc.presentation.dialogs.delete

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.CompletableFlowWithParam
import dev.olog.msc.core.coroutines.ComputationDispatcher
import dev.olog.msc.core.gateway.podcast.PodcastGateway
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.core.gateway.track.SongGateway
import javax.inject.Inject

class DeleteUseCase @Inject constructor(
    scheduler: ComputationDispatcher,
    private val playlistGateway: PlaylistGateway,
    private val podcastGateway: PodcastGateway,
    private val songGateway: SongGateway

) : CompletableFlowWithParam<MediaId>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun buildUseCaseObservable(mediaId: MediaId) {
        if (mediaId.isLeaf && mediaId.isPodcast) {
            return podcastGateway.deleteSingle(mediaId.resolveId)
        }

        if (mediaId.isLeaf) {
            return songGateway.deleteSingle(mediaId.resolveId)
        }

        return when {
            mediaId.isPodcastPlaylist -> playlistGateway.deletePlaylist(mediaId.categoryValue.toLong())
            mediaId.isPlaylist -> playlistGateway.deletePlaylist(mediaId.categoryValue.toLong())
            else -> TODO("")
//            else -> getSongListByParamUseCase.execute(mediaId)
//                .flatMapCompletable { songGateway.deleteGroup(it) }
        }
    }
}