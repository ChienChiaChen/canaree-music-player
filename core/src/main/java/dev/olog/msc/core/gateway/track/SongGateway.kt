package dev.olog.msc.core.gateway.track

import dev.olog.msc.core.entity.data.request.ItemRequest
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.base.BaseGateway
import kotlinx.coroutines.flow.Flow

interface SongGateway : BaseGateway<Song, Long> {

    fun getByAlbumId(albumId: Long): ItemRequest<Song>

    fun getAllUnfiltered(): Flow<List<Song>>

    // handles song and podcast
    fun deleteSingle(songId: Long)
    // handles song and podcast
    fun deleteGroup(songList: List<Long>)

    suspend fun getUneditedByParam(songId: Long): Flow<Song>

    // handles song and podcast
    suspend fun getByUri(uri: String): Song?

}