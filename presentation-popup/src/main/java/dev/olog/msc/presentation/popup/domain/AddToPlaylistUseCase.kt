package dev.olog.msc.presentation.popup.domain

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.track.Playlist
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.core.interactor.GetSongListByParamUseCase
import dev.olog.msc.core.interactor.base.CompletableUseCaseWithParam
import dev.olog.msc.core.interactor.item.GetPodcastUseCase
import dev.olog.msc.core.interactor.item.GetSongUseCase
import dev.olog.msc.shared.extensions.mapToList
import io.reactivex.Completable
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

class AddToPlaylistUseCase @Inject constructor(
    scheduler: IoScheduler,
    private val playlistGateway: PlaylistGateway,
    private val getSongUseCase: GetSongUseCase,
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val getPodcastUseCase: GetPodcastUseCase,
    private val getSongListByParamUseCase: GetSongListByParamUseCase

) : CompletableUseCaseWithParam<Pair<Playlist, MediaId>>(scheduler) {

    override fun buildUseCaseObservable(param: Pair<Playlist, MediaId>): Completable = runBlocking{
        val (playlist, mediaId) = param

        if (mediaId.isLeaf && mediaId.isPodcast){
            getPodcastUseCase.execute(mediaId).asObservable()
                    .firstOrError()
                    .flatMapCompletable { podcastPlaylistGateway.addSongsToPlaylist(playlist.id, listOf(mediaId.resolveId)) }
        } else if  (mediaId.isLeaf) {
            getSongUseCase.execute(mediaId).asObservable()
                    .firstOrError()
                    .flatMapCompletable { playlistGateway.addSongsToPlaylist(playlist.id, listOf(mediaId.resolveId)) }
        } else {
            getSongListByParamUseCase.execute(mediaId)
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
}