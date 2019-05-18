package dev.olog.msc.core.coroutines

import kotlinx.coroutines.withContext

abstract class CompletableFlow(
    private val schedulers: Dispatcher
) {

    protected abstract suspend fun buildUseCaseObservable()

    suspend fun execute() = withContext(schedulers.worker) {
        buildUseCaseObservable()
    }

}