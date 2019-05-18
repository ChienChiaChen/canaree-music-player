package dev.olog.msc.core.gateway.track

import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.entity.track.Folder
import dev.olog.msc.core.gateway.base.*
import kotlinx.coroutines.flow.Flow

interface FolderGateway :
    BaseGateway<Folder, String>,
    ChildsHasSongs<String>,
    HasMostPlayed,
    HasRecentlyAddedSongs,
    HasSiblings<Folder>,
    HasRelatedArtists<Artist> {

    fun getAllUnfiltered(): Flow<List<Folder>>

}