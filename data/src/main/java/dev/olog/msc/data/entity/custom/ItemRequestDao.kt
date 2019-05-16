package dev.olog.msc.data.entity.custom

import dev.olog.msc.core.entity.ItemRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class ItemRequestDao<T, Param>(
    private val getItemByParam: (Param) -> T?,
    private val observeItemByParam: (Param) -> Flow<T?>,
    private val param: Param
) : ItemRequest<T> {

    override fun getItem(): T? {
        return getItemByParam(param)
    }

    override suspend fun observeItem(): Flow<T?> {
        return observeItemByParam(param).distinctUntilChanged()
    }
}