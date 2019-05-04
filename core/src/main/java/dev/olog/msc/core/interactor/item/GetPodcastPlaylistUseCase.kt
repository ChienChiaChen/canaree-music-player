package dev.olog.msc.core.interactor.item

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.podcast.PodcastPlaylist
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.PodcastPlaylistGateway
import dev.olog.msc.core.interactor.base.ObservableUseCaseWithParam
import io.reactivex.Observable
import javax.inject.Inject

class GetPodcastPlaylistUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: PodcastPlaylistGateway

) : ObservableUseCaseWithParam<PodcastPlaylist, MediaId>(schedulers) {

    override fun buildUseCaseObservable(param: MediaId): Observable<PodcastPlaylist> {
        return gateway.getByParam(param.categoryId)
    }
}