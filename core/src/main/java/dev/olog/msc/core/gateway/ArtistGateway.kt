package dev.olog.msc.core.gateway

import dev.olog.msc.core.entity.track.Artist

interface ArtistGateway :
        BaseGateway<Artist, Long>,
        ChildsHasSongs<Long>,
        HasLastPlayed<Artist>