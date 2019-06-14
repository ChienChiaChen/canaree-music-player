package dev.olog.msc.domain.interactor.search.delete

import dev.olog.msc.core.executor.IoScheduler
import dev.olog.msc.domain.gateway.RecentSearchesGateway
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