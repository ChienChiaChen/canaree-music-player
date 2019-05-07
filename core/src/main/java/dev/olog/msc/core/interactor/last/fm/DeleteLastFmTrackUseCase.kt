package dev.olog.msc.core.interactor.last.fm

import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.LastFmGateway
import dev.olog.msc.core.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class DeleteLastFmTrackUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: LastFmGateway

): CompletableUseCaseWithParam<Pair<Long, Boolean>>(schedulers) {

    override fun buildUseCaseObservable(param: Pair<Long, Boolean>): Completable {
        val (artistId, isPodcast) = param
        return Completable.fromCallable {
            if (isPodcast){
                gateway.deletePodcast(artistId)
            } else {
                gateway.deleteTrack(artistId)
            }

        }
    }
}