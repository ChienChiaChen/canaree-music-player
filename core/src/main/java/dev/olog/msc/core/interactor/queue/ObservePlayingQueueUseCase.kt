package dev.olog.msc.core.interactor.queue

import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.coroutines.ObservableFlow
import dev.olog.msc.core.entity.PlayingQueueSong
import dev.olog.msc.core.gateway.PlayingQueueGateway
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePlayingQueueUseCase @Inject constructor(
    scheduler: IoDispatcher,
    private val gateway: PlayingQueueGateway

) : ObservableFlow<List<PlayingQueueSong>>(scheduler) {

    override suspend fun buildUseCaseObservable(): Flow<List<PlayingQueueSong>> {
        return gateway.observeAll()
    }
}