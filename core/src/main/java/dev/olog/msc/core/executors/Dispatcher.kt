package dev.olog.msc.core.executors

import kotlin.coroutines.CoroutineContext

interface Dispatcher {
    val worker: CoroutineContext
}