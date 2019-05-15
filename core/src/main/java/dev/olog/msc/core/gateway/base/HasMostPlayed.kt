package dev.olog.msc.core.gateway.base

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.ChunkedData
import dev.olog.msc.core.entity.track.Song
import io.reactivex.Completable

interface HasMostPlayed {

    fun canShowMostPlayed(mediaId: MediaId): Boolean
    fun getMostPlayedChunk(mediaId: MediaId): ChunkedData<Song>
    fun insertMostPlayed(mediaId: MediaId): Completable

}