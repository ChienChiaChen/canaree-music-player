package dev.olog.msc.core.coroutines

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch


fun <T, R> Flow<T>.merge(vararg other: Flow<R>): Flow<Unit> = flow {
    coroutineScope {
        launch {
            this@merge.collect {
                emit(Unit)
            }
        }
        for (flow in other) {
            launch {
                flow.collect {
                    emit(Unit)
                }
            }
        }
    }
}