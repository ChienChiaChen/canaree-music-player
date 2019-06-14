package dev.olog.msc.domain.interactor.last.fm

import dev.olog.msc.app.IoSchedulers
import dev.olog.msc.core.gateway.LastFmGateway
import dev.olog.msc.core.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class DeleteLastFmTrackUseCase @Inject constructor(
    schedulers: IoSchedulers,
    private val gateway: LastFmGateway

) : CompletableUseCaseWithParam<Long>(schedulers) {

    override fun buildUseCaseObservable(param: Long): Completable {
        return Completable.fromCallable {
            gateway.deleteTrack(param)
        }
    }
}