package dev.olog.msc.shared.core.flow

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

fun <T, R, S> Flow<T>.withLatest(otherFlow: Flow<R>, combiner: (T, R) -> S): Flow<S> {
    return flow {
        coroutineScope {
            var current: T? = null
            var other: R? = null
            var neverEmitted = true
            launch {
                this@withLatest.collect {
                    current = it
                    if (other != null) {
                        emit(combiner(current!!, other!!))
                        neverEmitted = false
                    }
                }
            }
            launch {
                otherFlow.collect {
                    other = it
                    if (neverEmitted && current != null) {
                        emit(combiner(current!!, other!!))
                        neverEmitted = false
                    }
                }
            }

        }
    }
}

