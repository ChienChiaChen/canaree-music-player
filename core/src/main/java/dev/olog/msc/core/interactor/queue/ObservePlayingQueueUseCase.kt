package dev.olog.msc.core.interactor.queue

import dev.olog.msc.core.entity.PlayingQueueSong
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.PlayingQueueGateway
import dev.olog.msc.core.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class ObservePlayingQueueUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val gateway: PlayingQueueGateway

) : ObservableUseCase<List<PlayingQueueSong>>(scheduler) {

    override fun buildUseCaseObservable(): Observable<List<PlayingQueueSong>> {
        return gateway.observeAll()
    }
}