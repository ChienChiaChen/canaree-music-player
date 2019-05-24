package dev.olog.msc.presentation.search.domain

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.executors.ComputationDispatcher
import dev.olog.msc.core.gateway.RecentSearchesGateway
import dev.olog.msc.core.interactor.base.CompletableFlowWithParam
import javax.inject.Inject

class DeleteRecentSearchUseCase @Inject constructor(
    scheduler: ComputationDispatcher,
    private val recentSearchesGateway: RecentSearchesGateway

) : CompletableFlowWithParam<MediaId>(scheduler) {

    override suspend fun buildUseCaseObservable(mediaId: MediaId) {
        val id = mediaId.resolveId
        return when {
            mediaId.isLeaf && !mediaId.isPodcast -> recentSearchesGateway.deleteSong(id)
            mediaId.isArtist -> recentSearchesGateway.deleteArtist(id)
            mediaId.isAlbum -> recentSearchesGateway.deleteAlbum(id)
            mediaId.isPlaylist -> recentSearchesGateway.deletePlaylist(id)
            mediaId.isFolder -> recentSearchesGateway.deleteFolder(id)
            mediaId.isGenre -> recentSearchesGateway.deleteGenre(id)

            mediaId.isLeaf && mediaId.isPodcast -> recentSearchesGateway.deletePodcast(id)
            mediaId.isPodcastPlaylist -> recentSearchesGateway.deletePodcastPlaylist(id)
            mediaId.isPodcastAlbum -> recentSearchesGateway.deletePodcastAlbum(id)
            mediaId.isPodcastArtist -> recentSearchesGateway.deletePodcastArtist(id)
            else -> throw IllegalArgumentException("invalid category ${mediaId.resolveId}")
        }
    }
}