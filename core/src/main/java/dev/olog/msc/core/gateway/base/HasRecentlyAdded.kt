package dev.olog.msc.core.gateway.base

import dev.olog.msc.core.entity.data.request.DataRequest
import dev.olog.msc.core.entity.data.request.Filter


interface HasRecentlyAdded<T>{

    fun getRecentlyAdded(): DataRequest<T>
    fun canShowRecentlyAdded(filter: Filter): Boolean

}