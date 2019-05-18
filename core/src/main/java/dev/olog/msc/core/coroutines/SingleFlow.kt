package dev.olog.msc.core.coroutines

import kotlinx.coroutines.withContext

abstract class SingleFlow<T>(
    private val schedulers: Dispatcher
) {

    protected abstract suspend fun buildUseCaseObservable(): T

    suspend fun execute(): T = withContext(schedulers.worker) {
        buildUseCaseObservable()
    }

}