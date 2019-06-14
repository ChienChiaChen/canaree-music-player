package dev.olog.msc.domain.interactor.all.sibling

import dev.olog.msc.core.entity.PodcastAlbum
import dev.olog.msc.core.executor.IoScheduler
import dev.olog.msc.domain.gateway.PodcastAlbumGateway
import dev.olog.msc.core.interactor.base.ObservableUseCaseWithParam
import dev.olog.msc.core.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class GetPodcastAlbumSiblingsByArtistUseCase @Inject internal constructor(
    schedulers: IoScheduler,
    private val albumGateway: PodcastAlbumGateway

) : ObservableUseCaseWithParam<List<PodcastAlbum>, MediaId>(schedulers) {


    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<PodcastAlbum>> {
        val artistId = mediaId.categoryValue.toLong()
        return albumGateway.observeByArtist(artistId)
    }
}