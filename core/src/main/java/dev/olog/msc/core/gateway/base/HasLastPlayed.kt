package dev.olog.msc.core.gateway.base

import dev.olog.msc.core.entity.ChunkedData
import io.reactivex.Completable

interface HasLastPlayed<T> {

    fun getLastPlayedChunk(): ChunkedData<T>
    fun canShowLastPlayed(): Boolean

    fun addLastPlayed(id: Long): Completable

}