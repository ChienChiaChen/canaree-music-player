package dev.olog.msc.domain.gateway

import dev.olog.msc.core.entity.PodcastArtist
import dev.olog.msc.core.gateway.BaseGateway
import dev.olog.msc.core.gateway.HasLastPlayed


interface PodcastArtistGateway :
        BaseGateway<PodcastArtist, Long>,
        ChildsHasPodcasts<Long>,
    HasLastPlayed<PodcastArtist>