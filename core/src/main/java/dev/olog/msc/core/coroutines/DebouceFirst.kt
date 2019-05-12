package dev.olog.msc.core.coroutines

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select

fun <T> Flow<T>.debounceFirst(timeoutMillis: Long): Flow<T> {
    require(timeoutMillis > 0) { "Debounce timeout should be positive" }
    return flow {
        coroutineScope {
            val values = Channel<Any?>(Channel.CONFLATED) // Actually Any, KT-30796
            // Channel is not closed deliberately as there is no close with value
            val collector = launch {
                try {
                    collect { value -> values.send(value ?: NullSurrogate) }
                } catch (e: Throwable) {
                    values.close(e) // Workaround for #1130
                    throw e
                }
            }

            var firstEmission = true
            var isDone = false
            var lastValue: Any? = null
            while (!isDone) {
                select<Unit> {
                    values.onReceive {
                        lastValue = it
                    }

                    lastValue?.let { value ->
                        // set timeout when lastValue != null
                        if (firstEmission) {
                            onTimeout(0) {
                                lastValue = null // Consume the value
                                emit(NullSurrogate.unbox(value))
                                firstEmission = false
                            }
                        } else {
                            onTimeout(timeoutMillis) {
                                lastValue = null // Consume the value
                                emit(NullSurrogate.unbox(value))
                            }
                        }
                    }

                    // Close with value 'idiom'
                    collector.onJoin {
                        if (lastValue != null) emit(NullSurrogate.unbox(lastValue))
                        isDone = true
                    }
                }
            }
        }
    }
}

internal object NullSurrogate {

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    internal fun <T> unbox(value: Any?): T = if (value === NullSurrogate) null as T else value as T
}