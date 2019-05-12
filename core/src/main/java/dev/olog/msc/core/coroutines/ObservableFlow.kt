package dev.olog.msc.core.coroutines

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

abstract class ObservableFlow<T>(
    private val schedulers: Dispatcher
) {

    protected abstract suspend fun buildUseCaseObservable(): Flow<T>

    suspend fun execute(): Flow<T> = withContext(schedulers.worker) {
        buildUseCaseObservable()
    }

}