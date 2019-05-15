package dev.olog.msc.core.gateway.base

import dev.olog.msc.core.entity.ChunkedData
import kotlinx.coroutines.flow.Flow


interface BaseGateway<T, in Params> {

    fun getChunk(): ChunkedData<T>

    suspend fun getAll(): Flow<List<T>>

    fun getByParam(param: Params): T
    suspend fun observeByParam(param: Params): Flow<T>

}

