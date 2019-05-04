package dev.olog.msc.domain.interactor.search

import dev.olog.msc.core.entity.SearchResult
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.RecentSearchesGateway
import dev.olog.msc.core.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class GetAllRecentSearchesUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val recentSearchesGateway: RecentSearchesGateway

) : ObservableUseCase<List<SearchResult>>(scheduler) {

    override fun buildUseCaseObservable(): Observable<List<SearchResult>> {
        return recentSearchesGateway.getAll()
    }
}