package dev.olog.msc.domain.interactor.all.sorted.util

import dev.olog.msc.core.executor.IoScheduler
import dev.olog.msc.core.prefs.AppPreferencesGateway
import dev.olog.msc.core.interactor.base.CompletableUseCase
import io.reactivex.Completable
import javax.inject.Inject

class SetSortArrangingUseCase @Inject constructor(
    scheduler: IoScheduler,
    private val gateway: AppPreferencesGateway

) : CompletableUseCase(scheduler) {

    override fun buildUseCaseObservable(): Completable {
        return gateway.toggleSortArranging()
    }
}