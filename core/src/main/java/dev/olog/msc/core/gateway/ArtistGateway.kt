package dev.olog.msc.core.gateway

import dev.olog.msc.core.entity.Artist

interface ArtistGateway :
        BaseGateway<Artist, Long>,
    ChildsHasSongs<Long>,
    HasLastPlayed<Artist>