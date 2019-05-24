package dev.olog.msc.core.interactor.base

import dev.olog.msc.core.executors.Dispatcher
import kotlinx.coroutines.withContext

abstract class SingleFlow<T>(
    private val schedulers: Dispatcher
) {

    protected abstract suspend fun buildUseCaseObservable(): T

    suspend fun execute(): T = withContext(schedulers.worker) {
        buildUseCaseObservable()
    }

}