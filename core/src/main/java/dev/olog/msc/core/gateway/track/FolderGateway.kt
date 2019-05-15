package dev.olog.msc.core.gateway.track

import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.entity.track.Folder
import dev.olog.msc.core.gateway.base.*
import io.reactivex.Observable

interface FolderGateway :
    BaseGateway<Folder, String>,
    ChildsHasSongs<String>,
    HasMostPlayed,
    HasRecentlyAddedSongs,
    HasSiblings<Folder>,
    HasRelatedArtists<Artist> {

    fun getAllUnfiltered(): Observable<List<Folder>>

}