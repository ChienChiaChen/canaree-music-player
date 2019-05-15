package dev.olog.msc.core.gateway.base

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.ChunkedData

interface HasRelatedArtists<T> {

    fun getRelatedArtistsChunk(mediaId: MediaId): ChunkedData<T>
    fun getRelatedArtistsSize(mediaId: MediaId): Int
    fun canShowRelatedArtists(mediaId: MediaId): Boolean

}