package dev.olog.msc.core.gateway.base

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.ChunkedData
import dev.olog.msc.core.entity.track.Song

interface HasRecentlyAddedSongs {

    fun getRecentlyAddedSongsChunk(mediaId: MediaId): ChunkedData<Song>
    fun canShowRecentlyAddedSongs(mediaId: MediaId): Boolean
    fun getRecentlyAddedSongsSize(mediaId: MediaId): Int

}