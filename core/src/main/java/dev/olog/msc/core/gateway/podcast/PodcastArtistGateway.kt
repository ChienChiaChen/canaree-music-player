package dev.olog.msc.core.gateway.podcast

import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.entity.podcast.PodcastArtist
import dev.olog.msc.core.gateway.base.*


interface PodcastArtistGateway :
    BaseGateway<PodcastArtist, Long>,
    ChildsHasPodcasts<Long>,
    HasLastPlayed<PodcastArtist>,
    HasRecentlyAdded<PodcastArtist>,
    HasSiblings<PodcastAlbum>