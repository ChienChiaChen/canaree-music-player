package dev.olog.msc.core.gateway.base

import dev.olog.msc.core.entity.data.request.DataRequest

interface HasLastPlayed<T> {

    fun getLastPlayed(): DataRequest<T>
    fun canShowLastPlayed(): Boolean

    suspend fun addLastPlayed(id: Long)

}