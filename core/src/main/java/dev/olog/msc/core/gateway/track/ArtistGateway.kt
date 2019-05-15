package dev.olog.msc.core.gateway.track

import dev.olog.msc.core.entity.track.Album
import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.gateway.base.*

interface ArtistGateway :
    BaseGateway<Artist, Long>,
    ChildsHasSongs<Long>,
    HasLastPlayed<Artist>,
    HasRecentlyAdded<Artist>,
    HasSiblings<Album>