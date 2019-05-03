package dev.olog.msc.core.gateway

import dev.olog.msc.core.entity.track.Folder
import io.reactivex.Observable

interface FolderGateway :
        BaseGateway<Folder, String>,
        ChildsHasSongs<String>,
        HasMostPlayed {

    fun getAllUnfiltered(): Observable<List<Folder>>

}