package dev.olog.msc.domain.gateway

import dev.olog.msc.core.entity.PodcastAlbum
import dev.olog.msc.core.gateway.BaseGateway
import dev.olog.msc.core.gateway.HasLastPlayed
import io.reactivex.Observable

interface PodcastAlbumGateway :
        BaseGateway<PodcastAlbum, Long>,
        ChildsHasPodcasts<Long>,
    HasLastPlayed<PodcastAlbum> {

    fun observeByArtist(artistId: Long) : Observable<List<PodcastAlbum>>

}