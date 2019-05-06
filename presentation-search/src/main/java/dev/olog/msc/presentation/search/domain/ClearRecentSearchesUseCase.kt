package dev.olog.msc.presentation.search.domain

import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.RecentSearchesGateway
import dev.olog.msc.core.interactor.base.CompletableUseCase
import io.reactivex.Completable
import javax.inject.Inject

class ClearRecentSearchesUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val recentSearchesGateway: RecentSearchesGateway

) : CompletableUseCase(scheduler) {

    override fun buildUseCaseObservable(): Completable {
        return recentSearchesGateway.deleteAll()
    }
}