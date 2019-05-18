package dev.olog.msc.core.gateway.base

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.data.request.DataRequest
import dev.olog.msc.core.entity.track.Song

interface HasRecentlyAddedSongs {

    fun getRecentlyAddedSongs(mediaId: MediaId): DataRequest<Song>
    fun canShowRecentlyAddedSongs(mediaId: MediaId): Boolean

}