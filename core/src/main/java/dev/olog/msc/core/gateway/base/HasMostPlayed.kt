package dev.olog.msc.core.gateway.base

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.PageRequest
import dev.olog.msc.core.entity.track.Song

interface HasMostPlayed {

    fun canShowMostPlayed(mediaId: MediaId): Boolean
    fun getMostPlayed(mediaId: MediaId): PageRequest<Song>
    suspend fun insertMostPlayed(mediaId: MediaId)

}