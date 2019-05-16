@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.msc.core.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

inline fun <T, R> Flow<List<T>>.mapToList(crossinline mapper: (T) -> R): Flow<List<R>> {
    return this.map { it.map { mapper(it) } }
}

@Suppress("FunctionName")
fun CustomScope(dispatcher: CoroutineDispatcher = Dispatchers.Default): CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)