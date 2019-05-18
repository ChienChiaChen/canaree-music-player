package dev.olog.msc.presentation.search.domain

import dev.olog.msc.core.coroutines.CompletableFlow
import dev.olog.msc.core.coroutines.ComputationDispatcher
import dev.olog.msc.core.gateway.RecentSearchesGateway
import javax.inject.Inject

class ClearRecentSearchesUseCase @Inject constructor(
    scheduler: ComputationDispatcher,
    private val recentSearchesGateway: RecentSearchesGateway

) : CompletableFlow(scheduler) {

    override suspend fun buildUseCaseObservable() {
        return recentSearchesGateway.deleteAll()
    }
}