package dev.olog.msc.core.gateway.base

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.ChunkedData

interface HasSiblings<T> {
    fun getSiblingsChunk(mediaId: MediaId): ChunkedData<T>
    fun canShowSiblings(mediaId: MediaId): Boolean
}