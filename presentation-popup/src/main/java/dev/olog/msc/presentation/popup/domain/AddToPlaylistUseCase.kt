package dev.olog.msc.presentation.popup.domain

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.getAll
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.track.Playlist
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.executors.ComputationDispatcher
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.core.interactor.GetSongListChunkByParamUseCase
import dev.olog.msc.core.interactor.base.CompletableFlowWithParam
import javax.inject.Inject

class AddToPlaylistUseCase @Inject constructor(
    scheduler: ComputationDispatcher,
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val getSongListByParamUseCase: GetSongListChunkByParamUseCase

) : CompletableFlowWithParam<Pair<Playlist, MediaId>>(scheduler) {

    override suspend fun buildUseCaseObservable(param: Pair<Playlist, MediaId>) {
        val (playlist, mediaId) = param

        if (mediaId.isLeaf && mediaId.isPodcast) {
            return podcastPlaylistGateway.addSongsToPlaylist(playlist.id, listOf(mediaId.resolveId))
        } else if (mediaId.isLeaf) {
            return playlistGateway.addSongsToPlaylist(playlist.id, listOf(mediaId.resolveId))
        } else {
            val songList = getSongListByParamUseCase.execute(mediaId).getAll(Filter.NO_FILTER)
                .map {
                    when (it) {
                        is Song -> it.id
                        is Podcast -> it.id
                        else -> throw Exception("invalid item type $it")
                    }
                }
            if (mediaId.isAnyPodcast) {
                podcastPlaylistGateway.addSongsToPlaylist(playlist.id, songList)
            } else {
                playlistGateway.addSongsToPlaylist(playlist.id, songList)
            }
        }
    }
}