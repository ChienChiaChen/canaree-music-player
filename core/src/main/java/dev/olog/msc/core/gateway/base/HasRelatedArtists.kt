package dev.olog.msc.core.gateway.base

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.PageRequest

interface HasRelatedArtists<T> {

    fun getRelatedArtists(mediaId: MediaId): PageRequest<T>
    fun canShowRelatedArtists(mediaId: MediaId): Boolean

}