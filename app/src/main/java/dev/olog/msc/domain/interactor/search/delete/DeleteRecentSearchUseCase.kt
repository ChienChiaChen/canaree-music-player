package dev.olog.msc.domain.interactor.search.delete

import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.RecentSearchesGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import dev.olog.msc.core.MediaId
import io.reactivex.Completable
import javax.inject.Inject

class DeleteRecentSearchUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val recentSearchesGateway: RecentSearchesGateway

) : CompletableUseCaseWithParam<MediaId>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Completable {
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