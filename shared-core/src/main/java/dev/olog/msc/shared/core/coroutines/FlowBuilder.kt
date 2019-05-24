package dev.olog.msc.shared.core.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit

fun flowInterval(interval: Long, timeUnit: TimeUnit): Flow<Int> {
    return flow {
        var tick = 0
        emit(tick)
        while (true){
            delay(timeUnit.toMillis(interval))
            emit(++tick)
        }
    }
}