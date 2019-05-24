package dev.olog.msc.shared.core.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

inline fun <T, R> Flow<List<T>>.mapToList(crossinline mapper: (T) -> R): Flow<List<R>> {
    return this.map { it.map { mapper(it) } }
}