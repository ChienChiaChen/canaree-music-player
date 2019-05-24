package dev.olog.msc.presentation.search.domain

import dev.olog.msc.core.executors.ComputationDispatcher
import dev.olog.msc.core.gateway.RecentSearchesGateway
import dev.olog.msc.core.interactor.base.CompletableFlow
import javax.inject.Inject

class ClearRecentSearchesUseCase @Inject constructor(
    scheduler: ComputationDispatcher,
    private val recentSearchesGateway: RecentSearchesGateway

) : CompletableFlow(scheduler) {

    override suspend fun buildUseCaseObservable() {
        return recentSearchesGateway.deleteAll()
    }
}