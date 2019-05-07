package dev.olog.msc.presentation.popup.domain

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.track.Playlist
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.PlaylistGateway
import dev.olog.msc.core.gateway.PodcastPlaylistGateway
import dev.olog.msc.core.interactor.GetSongListByParamUseCase
import dev.olog.msc.core.interactor.base.CompletableUseCaseWithParam
import dev.olog.msc.core.interactor.item.GetPodcastUseCase
import dev.olog.msc.core.interactor.item.GetSongUseCase
import dev.olog.msc.shared.extensions.mapToList
import io.reactivex.Completable
import javax.inject.Inject

class AddToPlaylistUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val playlistGateway: PlaylistGateway,
        private val getSongUseCase: GetSongUseCase,
        private val podcastPlaylistGateway: PodcastPlaylistGateway,
        private val getPodcastUseCase: GetPodcastUseCase,
        private val getSongListByParamUseCase: GetSongListByParamUseCase

) : CompletableUseCaseWithParam<Pair<Playlist, MediaId>>(scheduler) {

    override fun buildUseCaseObservable(param: Pair<Playlist, MediaId>): Completable {
        val (playlist, mediaId) = param

        if (mediaId.isLeaf && mediaId.isPodcast){
            return getPodcastUseCase.execute(mediaId)
                    .firstOrError()
                    .flatMapCompletable { podcastPlaylistGateway.addSongsToPlaylist(playlist.id, listOf(mediaId.resolveId)) }
        }

        if (mediaId.isLeaf) {
            return getSongUseCase.execute(mediaId)
                    .firstOrError()
                    .flatMapCompletable { playlistGateway.addSongsToPlaylist(playlist.id, listOf(mediaId.resolveId)) }
        }

        return getSongListByParamUseCase.execute(mediaId)
                .firstOrError()
                .mapToList { it.id }
                .flatMapCompletable {
                    if (mediaId.isAnyPodcast){
                        podcastPlaylistGateway.addSongsToPlaylist(playlist.id, it)
                    } else {
                        playlistGateway.addSongsToPlaylist(playlist.id, it)
                    }

                }
    }
}