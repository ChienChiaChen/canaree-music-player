package dev.olog.msc.domain.interactor.base

import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.core.gateway.BaseGateway
import io.reactivex.Observable

abstract class GetGroupUseCase<T>(
        private val gateway: BaseGateway<T, *>,
        schedulers: ComputationScheduler
) : ObservableUseCase<List<T>>(schedulers) {


    override fun buildUseCaseObservable(): Observable<List<T>> = gateway.getAll()
}