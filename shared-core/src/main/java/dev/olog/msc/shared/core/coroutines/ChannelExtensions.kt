package dev.olog.msc.shared.core.coroutines

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

fun <T, R, S> combineLatest(flow1: ReceiveChannel<T>, flow2: ReceiveChannel<R>, combiner: (T, R) -> S): Flow<S> {
    return flow {
        coroutineScope {
            var result1: T? = null
            var result2: R? = null
            launch {
                for (t in flow1) {
                    result1 = t
                    if (result1 != null && result2 != null) {
                        emit(combiner(result1!!, result2!!))
                    }
                }
            }
            launch {
                for (r in flow2) {
                    result2 = r
                    if (result1 != null && result2 != null) {
                        emit(combiner(result1!!, result2!!))
                    }
                }
            }
        }
    }
}

fun <T> ReceiveChannel<T>.asFlow(): Flow<T> {
    val channel = this
    return flow {
        coroutineScope {
            for (t in channel) {
                emit(t)
            }
        }
    }
}