package dev.olog.msc.core.gateway.base

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.PageRequest

interface HasSiblings<T> {
    fun getSiblings(mediaId: MediaId): PageRequest<T>
    fun canShowSiblings(mediaId: MediaId): Boolean
}