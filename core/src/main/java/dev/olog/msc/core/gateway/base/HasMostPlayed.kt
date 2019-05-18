package dev.olog.msc.core.gateway.base

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.data.request.DataRequest
import dev.olog.msc.core.entity.track.Song

interface HasMostPlayed {

    fun canShowMostPlayed(mediaId: MediaId): Boolean
    fun getMostPlayed(mediaId: MediaId): DataRequest<Song>
    suspend fun insertMostPlayed(mediaId: MediaId)

}