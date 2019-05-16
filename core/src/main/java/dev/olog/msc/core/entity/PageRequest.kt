package dev.olog.msc.core.entity

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow

interface PageRequest<T> {

    fun getPage(page: Page): List<T>
    fun getCount(): Int

    suspend fun observePage(page: Page): Flow<List<T>>
    suspend fun observeNotification(): Flow<Unit>

}

fun <T> PageRequest<T>.getCountAsync(): Deferred<Int> = GlobalScope.async { getCount() }

fun <T> PageRequest<T>.getPageAsync(page: Page): Deferred<List<T>> = GlobalScope.async { getPage(page) }

fun <T> PageRequest<T>.getAll(): List<T> = getPage(Page(0, Int.MAX_VALUE))
fun <T> PageRequest<T>.getAllAsync(): Deferred<List<T>> = getPageAsync(Page(0, Int.MAX_VALUE))

suspend fun <T> PageRequest<T>.observeAll(): Flow<List<T>> = observePage(Page(0, Int.MAX_VALUE))

class Page(
    val offset: Int,
    val limit: Int
)