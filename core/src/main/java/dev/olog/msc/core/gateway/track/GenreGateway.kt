package dev.olog.msc.core.gateway.track

import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.entity.track.Genre
import dev.olog.msc.core.gateway.base.*

interface GenreGateway :
    BaseGateway<Genre, Long>,
    ChildsHasSongs<Long>,
    HasMostPlayed,
    HasRecentlyAddedSongs,
    HasSiblings<Genre>,
    HasRelatedArtists<Artist>
