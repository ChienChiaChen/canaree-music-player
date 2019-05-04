package dev.olog.msc.core.interactor.queue

import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.PlayingQueueGateway
import dev.olog.msc.core.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class UpdateMiniQueueUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: PlayingQueueGateway
) : CompletableUseCaseWithParam<List<Pair<Int, Long>>>(schedulers) {

    override fun buildUseCaseObservable(param: List<Pair<Int, Long>>): Completable {
        return Completable.fromCallable {
            gateway.updateMiniQueue(param)
        }
    }
}