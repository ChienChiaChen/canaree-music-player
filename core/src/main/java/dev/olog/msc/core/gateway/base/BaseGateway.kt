package dev.olog.msc.core.gateway.base

import dev.olog.msc.core.entity.data.request.DataRequest
import dev.olog.msc.core.entity.data.request.ItemRequest


interface BaseGateway<T, in Params> {

    fun getAll(): DataRequest<T>
    fun getByParam(param: Params): ItemRequest<T>

}

