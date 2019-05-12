package dev.olog.msc.core.coroutines

import kotlin.coroutines.CoroutineContext

interface Dispatcher {
    val worker: CoroutineContext
}