package dev.olog.msc.shared.core.coroutines

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

fun <T, R, S> combineLatest(flow1: Flow<T>, flow2: Flow<R>, combiner: (T, R) -> S): Flow<S> {
    return flow {
        coroutineScope {
            var result1: T? = null
            var result2: R? = null
            launch {
                flow1.collect {
                    result1 = it
                    if (result1 != null && result2 != null) {
                        emit(combiner(result1!!, result2!!))
                    }
                }
            }
            launch {
                flow2.collect {
                    result2 = it
                    if (result1 != null && result2 != null) {
                        emit(combiner(result1!!, result2!!))
                    }
                }
            }
        }
    }
}



fun <T, R, S, U> combineLatest(flow1: Flow<T>, flow2: Flow<R>, flow3: Flow<S>, combiner: (T, R, S) -> U): Flow<U> {
    return flow {
        coroutineScope {
            var result1: T? = null
            var result2: R? = null
            var result3: S? = null
            launch {
                flow1.collect {
                    result1 = it
                    if (result1 != null && result2 != null && result3 != null) {
                        emit(combiner(result1!!, result2!!, result3!!))
                    }
                }
            }
            launch {
                flow2.collect {
                    result2 = it
                    if (result1 != null && result2 != null && result3 != null) {
                        emit(combiner(result1!!, result2!!, result3!!))
                    }
                }
            }
            launch {
                flow3.collect {
                    result3 = it
                    if (result1 != null && result2 != null && result3 != null) {
                        emit(combiner(result1!!, result2!!, result3!!))
                    }
                }
            }
        }
    }
}


