package dev.olog.msc.presentation.home.domain

import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.coroutines.ObservableFlow
import dev.olog.msc.core.coroutines.debounceFirst
import dev.olog.msc.core.entity.data.request.Page
import dev.olog.msc.core.gateway.PlayingQueueGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class IsRepositoryEmptyUseCase @Inject constructor(
    scheduler: IoDispatcher,
    private val playingQueueGateway: PlayingQueueGateway

) : ObservableFlow<Boolean>(scheduler) {


    override suspend fun buildUseCaseObservable(): Flow<Boolean> {
        return playingQueueGateway.observeAll(Page.NO_PAGING)
            .debounceFirst(250)
            .map { it.isEmpty() }
            .distinctUntilChanged()
    }
}