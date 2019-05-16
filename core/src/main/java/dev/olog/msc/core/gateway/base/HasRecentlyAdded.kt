package dev.olog.msc.core.gateway.base

import dev.olog.msc.core.entity.PageRequest


interface HasRecentlyAdded<T>{

    fun getRecentlyAdded(): PageRequest<T>
    fun canShowRecentlyAdded(): Boolean

}