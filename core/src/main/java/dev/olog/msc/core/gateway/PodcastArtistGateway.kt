package dev.olog.msc.core.gateway

import dev.olog.msc.core.entity.PodcastArtist


interface PodcastArtistGateway :
        BaseGateway<PodcastArtist, Long>,
    ChildsHasPodcasts<Long>,
    HasLastPlayed<PodcastArtist>