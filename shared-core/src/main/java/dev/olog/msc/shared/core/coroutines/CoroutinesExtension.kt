@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.msc.shared.core.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Suppress("FunctionName")
fun DefaultScope(dispatcher: CoroutineDispatcher = Dispatchers.Default): CoroutineScope =
    CoroutineScope(SupervisorJob() + dispatcher)