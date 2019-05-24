package dev.olog.msc.core.interactor.base

import dev.olog.msc.core.executors.Dispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

abstract class ObservableFlowWithParam<T, in Param>(
    private val schedulers: Dispatcher
) {

    protected abstract suspend fun buildUseCaseObservable(param: Param): Flow<T>

    suspend fun execute(param: Param): Flow<T> = withContext(schedulers.worker) {
        buildUseCaseObservable(param)
    }

}