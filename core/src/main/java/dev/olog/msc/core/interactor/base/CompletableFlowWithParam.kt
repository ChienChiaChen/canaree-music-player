package dev.olog.msc.core.interactor.base

import dev.olog.msc.core.executors.Dispatcher
import kotlinx.coroutines.withContext

abstract class CompletableFlowWithParam<in Param>(
    private val schedulers: Dispatcher
) {

    protected abstract suspend fun buildUseCaseObservable(param: Param)

    suspend fun execute(param: Param) = withContext(schedulers.worker) {
        buildUseCaseObservable(param)
    }

}