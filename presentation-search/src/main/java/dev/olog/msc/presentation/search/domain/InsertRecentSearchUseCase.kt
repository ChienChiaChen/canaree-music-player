package dev.olog.msc.presentation.search.domain

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.executors.ComputationDispatcher
import dev.olog.msc.core.gateway.RecentSearchesGateway
import dev.olog.msc.core.interactor.base.CompletableFlowWithParam
import javax.inject.Inject


class InsertRecentSearchUseCase @Inject constructor(
    scheduler: ComputationDispatcher,
    private val recentSearchesGateway: RecentSearchesGateway

) : CompletableFlowWithParam<MediaId>(scheduler) {

    override suspend fun buildUseCaseObservable(mediaId: MediaId) {
        val id = mediaId.resolveId
        return when {
            mediaId.isLeaf && !mediaId.isPodcast -> recentSearchesGateway.insertSong(id)
            mediaId.isArtist -> recentSearchesGateway.insertArtist(id)
            mediaId.isAlbum -> recentSearchesGateway.insertAlbum(id)
            mediaId.isPlaylist -> recentSearchesGateway.insertPlaylist(id)
            mediaId.isFolder -> recentSearchesGateway.insertFolder(id)
            mediaId.isGenre -> recentSearchesGateway.insertGenre(id)

            mediaId.isLeaf && mediaId.isPodcast -> recentSearchesGateway.insertPodcast(id)
            mediaId.isPodcastPlaylist -> recentSearchesGateway.insertPodcastPlaylist(id)
            mediaId.isPodcastAlbum -> recentSearchesGateway.insertPodcastAlbum(id)
            mediaId.isPodcastArtist -> recentSearchesGateway.insertPodcastArtist(id)
            else -> throw IllegalArgumentException("invalid category ${mediaId.resolveId}")
        }
    }
}