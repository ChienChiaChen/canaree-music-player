package dev.olog.msc.core.entity.data.request

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow

interface DataRequest<T> {

    fun getPage(request: Request): List<T>
    fun getCount(filter: Filter): Int

    suspend fun observePage(page: Request): Flow<List<T>>
    suspend fun observeNotification(): Flow<Unit>

}

fun <T> DataRequest<T>.getCountAsync(filter: Filter): Deferred<Int> = GlobalScope.async { getCount(filter) }

fun <T> DataRequest<T>.getPageAsync(request: Request): Deferred<List<T>> = GlobalScope.async { getPage(request) }

fun <T> DataRequest<T>.getAll(filter: Filter): List<T> = getPage(
    Request(Page.NO_PAGING, filter)
)

fun <T> DataRequest<T>.getAllAsync(filter: Filter): Deferred<List<T>> =
    getPageAsync(Request(Page.NO_PAGING, filter))

suspend fun <T> DataRequest<T>.observeAll(filter: Filter): Flow<List<T>> =
    observePage(Request(Page.NO_PAGING, filter))

