package dev.olog.msc.core.gateway.podcast

import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.gateway.base.*
import io.reactivex.Observable

interface PodcastAlbumGateway :
    BaseGateway<PodcastAlbum, Long>,
    ChildsHasPodcasts<Long>,
    HasLastPlayed<PodcastAlbum>,
    HasRecentlyAdded<PodcastAlbum>,
    HasSiblings<PodcastAlbum> {

    fun observeByArtist(artistId: Long): Observable<List<PodcastAlbum>>

}