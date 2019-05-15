package dev.olog.msc.core.gateway.track

import dev.olog.msc.core.entity.track.Album
import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.gateway.base.*
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow

interface AlbumGateway :
    BaseGateway<Album, Long>,
    ChildsHasSongs<Long>,
    HasLastPlayed<Album>,
    HasRecentlyAdded<Album>,
    HasSiblings<Album> {

    suspend fun observeArtistByAlbumId(albumId: Long): Flow<Artist>
    fun observeByArtist(artistId: Long): Observable<List<Album>>

}
