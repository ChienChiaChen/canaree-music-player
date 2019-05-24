package dev.olog.msc.presentation.home.domain

import dev.olog.msc.core.executors.IoDispatcher
import dev.olog.msc.core.gateway.PlayingQueueGateway
import dev.olog.msc.core.interactor.base.ObservableFlow
import dev.olog.msc.shared.core.flow.debounceFirst
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class IsPlayingQueueEmptyUseCase @Inject constructor(
    scheduler: IoDispatcher,
    private val playingQueueGateway: PlayingQueueGateway

) : ObservableFlow<Boolean>(scheduler) {


    override suspend fun buildUseCaseObservable(): Flow<Boolean> {
        return playingQueueGateway.isEmpty()
            .debounceFirst(250)
            .distinctUntilChanged()
    }
}