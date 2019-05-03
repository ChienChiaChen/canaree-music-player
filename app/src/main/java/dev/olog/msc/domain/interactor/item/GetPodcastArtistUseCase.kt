package dev.olog.msc.domain.interactor.item

import dev.olog.msc.core.entity.podcast.PodcastArtist
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.core.gateway.PodcastArtistGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCaseWithParam
import dev.olog.msc.core.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class GetPodcastArtistUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: PodcastArtistGateway

) : ObservableUseCaseWithParam<PodcastArtist, MediaId>(schedulers) {

    override fun buildUseCaseObservable(param: MediaId): Observable<PodcastArtist> {
        return gateway.getByParam(param.categoryId)
    }
}