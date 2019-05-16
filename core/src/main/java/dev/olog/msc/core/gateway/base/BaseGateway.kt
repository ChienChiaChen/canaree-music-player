package dev.olog.msc.core.gateway.base

import dev.olog.msc.core.entity.ItemRequest
import dev.olog.msc.core.entity.PageRequest


interface BaseGateway<T, in Params> {

    fun getAll(): PageRequest<T>
    fun getByParam(param: Params): ItemRequest<T>

}

