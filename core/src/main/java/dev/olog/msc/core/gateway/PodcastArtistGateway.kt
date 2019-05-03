package dev.olog.msc.core.gateway

import dev.olog.msc.core.entity.podcast.PodcastArtist


interface PodcastArtistGateway :
        BaseGateway<PodcastArtist, Long>,
        ChildsHasPodcasts<Long>,
        HasLastPlayed<PodcastArtist>