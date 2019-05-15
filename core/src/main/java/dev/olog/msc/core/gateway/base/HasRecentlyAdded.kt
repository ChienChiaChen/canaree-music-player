package dev.olog.msc.core.gateway.base

import dev.olog.msc.core.entity.ChunkedData


interface HasRecentlyAdded<T>{

    fun getRecentlyAddedChunk(): ChunkedData<T>
    fun canShowRecentlyAdded(): Boolean

}