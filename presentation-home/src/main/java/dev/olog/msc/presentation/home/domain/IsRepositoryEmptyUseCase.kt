package dev.olog.msc.presentation.home.domain

import dev.olog.msc.core.coroutines.ComputationDispatcher
import dev.olog.msc.core.coroutines.ObservableFlow
import dev.olog.msc.core.coroutines.debounceFirst
import dev.olog.msc.core.interactor.queue.ObservePlayingQueueUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class IsRepositoryEmptyUseCase @Inject constructor(
    scheduler: ComputationDispatcher,
    private val playingQueueUseCase: ObservePlayingQueueUseCase

) : ObservableFlow<Boolean>(scheduler) {


    override suspend fun buildUseCaseObservable(): Flow<Boolean> {
        return playingQueueUseCase.execute()
            .debounceFirst(250)
            .map { it.isEmpty() }
            .distinctUntilChanged()
    }
}