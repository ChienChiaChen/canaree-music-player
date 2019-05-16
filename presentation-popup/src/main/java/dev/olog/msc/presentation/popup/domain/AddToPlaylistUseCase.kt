package dev.olog.msc.presentation.popup.domain

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.CompletableFlowWithParam
import dev.olog.msc.core.coroutines.ComputationDispatcher
import dev.olog.msc.core.entity.track.Playlist
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.core.interactor.GetSongListByParamUseCase
import javax.inject.Inject

class AddToPlaylistUseCase @Inject constructor(
    scheduler: ComputationDispatcher,
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val getSongListByParamUseCase: GetSongListByParamUseCase

) : CompletableFlowWithParam<Pair<Playlist, MediaId>>(scheduler) {

    override suspend fun buildUseCaseObservable(param: Pair<Playlist, MediaId>){
        val (playlist, mediaId) = param

        if (mediaId.isLeaf && mediaId.isPodcast){
            return podcastPlaylistGateway.addSongsToPlaylist(playlist.id, listOf(mediaId.resolveId))
        } else if  (mediaId.isLeaf) {
            return playlistGateway.addSongsToPlaylist(playlist.id, listOf(mediaId.resolveId))
        } else {
            return TODO()
        }
//            getSongListByParamUseCase.execute(mediaId)
//                .firstOrError()
//                .mapToList { it.id }
//                .flatMapCompletable {
//                    if (mediaId.isAnyPodcast){
//                        podcastPlaylistGateway.addSongsToPlaylist(playlist.id, it)
//                    } else {
//                        playlistGateway.addSongsToPlaylist(playlist.id, it)
//                    }
//
//                }
//        }
    }
}