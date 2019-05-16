package dev.olog.msc.core.gateway.base

import dev.olog.msc.core.entity.PageRequest

interface HasLastPlayed<T> {

    fun getLastPlayed(): PageRequest<T>
    fun canShowLastPlayed(): Boolean

    suspend fun addLastPlayed(id: Long)

}