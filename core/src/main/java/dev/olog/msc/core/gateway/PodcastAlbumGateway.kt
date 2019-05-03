package dev.olog.msc.core.gateway

import dev.olog.msc.core.entity.podcast.PodcastAlbum
import io.reactivex.Observable

interface PodcastAlbumGateway :
        BaseGateway<PodcastAlbum, Long>,
        ChildsHasPodcasts<Long>,
        HasLastPlayed<PodcastAlbum> {

    fun observeByArtist(artistId: Long) : Observable<List<PodcastAlbum>>

}