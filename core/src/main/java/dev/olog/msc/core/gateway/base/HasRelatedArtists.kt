package dev.olog.msc.core.gateway.base

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.data.request.DataRequest
import dev.olog.msc.core.entity.data.request.Filter

interface HasRelatedArtists<T> {

    fun getRelatedArtists(mediaId: MediaId): DataRequest<T>
    fun canShowRelatedArtists(mediaId: MediaId, filter: Filter): Boolean

}