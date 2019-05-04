package dev.olog.msc.core.interactor.item

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.PodcastGateway
import dev.olog.msc.core.interactor.base.ObservableUseCaseWithParam
import io.reactivex.Observable
import javax.inject.Inject

class GetPodcastUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: PodcastGateway

) : ObservableUseCaseWithParam<Podcast, MediaId>(schedulers) {

    override fun buildUseCaseObservable(param: MediaId): Observable<Podcast> {
        return gateway.getByParam(param.resolveId)
    }
}
