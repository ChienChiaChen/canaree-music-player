package dev.olog.msc.data.entity.custom

import dev.olog.msc.core.entity.data.request.ItemRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class ItemRequestImmutable<T>(
    private val immutableItem :T?
) : ItemRequest<T> {

    override fun getItem(): T? {
        return immutableItem
    }

    override suspend fun observeItem(): Flow<T?> {
        return flowOf(getItem())
    }
}