package dev.olog.msc.core.interactor.sort

import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
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