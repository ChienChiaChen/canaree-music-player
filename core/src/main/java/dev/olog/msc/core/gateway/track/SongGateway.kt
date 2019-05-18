package dev.olog.msc.core.gateway.track

import dev.olog.msc.core.entity.data.request.ItemRequest
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.base.BaseGateway
import io.reactivex.Observable

interface SongGateway : BaseGateway<Song, Long> {

    suspend fun getByAlbumId(albumId: Long): ItemRequest<Song>

    fun getAllUnfiltered(): Observable<List<Song>>

    fun deleteSingle(songId: Long)

    fun deleteGroup(songList: List<Song>)

    fun getUneditedByParam(songId: Long): Observable<Song>

    // handles song and podcast
    suspend fun getByUri(uri: String): Song?

}