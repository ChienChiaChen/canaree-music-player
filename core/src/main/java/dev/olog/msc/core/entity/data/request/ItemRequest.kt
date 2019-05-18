package dev.olog.msc.core.entity.data.request

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow

interface ItemRequest<T> {

    fun getItem(): T?
    suspend fun observeItem(): Flow<T?>

}

fun <T> ItemRequest<T>.getItemAsync(): Deferred<T?> = GlobalScope.async { getItem() }