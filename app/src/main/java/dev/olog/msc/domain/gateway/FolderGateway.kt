package dev.olog.msc.domain.gateway

import dev.olog.msc.core.entity.Folder
import dev.olog.msc.core.gateway.BaseGateway
import dev.olog.msc.core.gateway.ChildsHasSongs
import io.reactivex.Observable

interface FolderGateway :
        BaseGateway<Folder, String>,
    ChildsHasSongs<String>,
        HasMostPlayed {

    fun getAllUnfiltered(): Observable<List<Folder>>

}