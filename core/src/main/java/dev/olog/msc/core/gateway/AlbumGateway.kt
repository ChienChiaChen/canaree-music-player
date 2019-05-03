package dev.olog.msc.core.gateway

import dev.olog.msc.core.entity.track.Album
import io.reactivex.Observable

interface AlbumGateway :
        BaseGateway<Album, Long>,
        ChildsHasSongs<Long>,
        HasLastPlayed<Album> {

    fun observeByArtist(artistId: Long) : Observable<List<Album>>

}
