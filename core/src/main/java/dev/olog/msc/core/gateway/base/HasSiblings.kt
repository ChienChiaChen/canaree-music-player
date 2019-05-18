package dev.olog.msc.core.gateway.base

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.data.request.DataRequest
import dev.olog.msc.core.entity.data.request.Filter

interface HasSiblings<T> {
    fun getSiblings(mediaId: MediaId): DataRequest<T>
    fun canShowSiblings(mediaId: MediaId, filter: Filter): Boolean
}