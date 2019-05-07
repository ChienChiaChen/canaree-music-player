package dev.olog.msc.presentation.detail.domain.siblings

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.PodcastAlbumGateway
import dev.olog.msc.core.interactor.base.ObservableUseCaseWithParam
import dev.olog.msc.core.interactor.item.GetPodcastAlbumUseCase
import io.reactivex.Observable
import javax.inject.Inject

class GetPodcastAlbumSiblingsByAlbumUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val getAlbumUseCase: GetPodcastAlbumUseCase,
        private val albumGateway: PodcastAlbumGateway

) : ObservableUseCaseWithParam<List<PodcastAlbum>, MediaId>(schedulers) {


    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<PodcastAlbum>> {
        val albumId = mediaId.categoryValue.toLong()
        return getAlbumUseCase.execute(mediaId)
                .map { it.artistId }
                .flatMap { artistId ->
                    albumGateway.observeByArtist(artistId)
                            .map { it.filter { it.id != albumId } }
                }
    }

}