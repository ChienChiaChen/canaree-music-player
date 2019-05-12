package dev.olog.msc.core.gateway

import dev.olog.msc.core.entity.ChunkedData
import kotlinx.coroutines.flow.Flow


interface BaseGateway<T, in Params> {

    fun getChunk(): ChunkedData<T>

    suspend fun getAll(): Flow<List<T>>

    suspend fun getByParam(param: Params): Flow<T>

}

