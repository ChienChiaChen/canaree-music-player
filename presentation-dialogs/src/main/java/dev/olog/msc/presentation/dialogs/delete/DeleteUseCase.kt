package dev.olog.msc.presentation.dialogs.delete

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.getAll
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.executors.ComputationDispatcher
import dev.olog.msc.core.gateway.podcast.PodcastGateway
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.core.gateway.track.SongGateway
import dev.olog.msc.core.interactor.GetSongListChunkByParamUseCase
import dev.olog.msc.core.interactor.base.CompletableFlowWithParam
import javax.inject.Inject

class DeleteUseCase @Inject constructor(
        scheduler: ComputationDispatcher,
        private val playlistGateway: PlaylistGateway,
        private val podcastGateway: PodcastGateway,
        private val songGateway: SongGateway,
        private val getSongListChunkByParamUseCase: GetSongListChunkByParamUseCase

) : CompletableFlowWithParam<MediaId>(scheduler) {

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
            else -> {
                val songs = getSongListChunkByParamUseCase.execute(mediaId).getAll(Filter.NO_FILTER)
                        .map {
                            when (it) {
                                is Song -> it.id
                                is Podcast -> it.id
                                else -> throw Exception("not supposed to happen")
                            }
                        }
                songGateway.deleteGroup(songs)
            }
        }
    }
}