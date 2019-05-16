package dev.olog.msc.core.gateway.track

import dev.olog.msc.core.entity.track.Album
import dev.olog.msc.core.gateway.base.*

interface AlbumGateway :
    BaseGateway<Album, Long>,
    ChildsHasSongs<Long>,
    HasLastPlayed<Album>,
    HasRecentlyAdded<Album>,
    HasSiblings<Album>
