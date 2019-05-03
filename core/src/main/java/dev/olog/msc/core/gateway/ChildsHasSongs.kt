package dev.olog.msc.core.gateway

import dev.olog.msc.core.entity.track.Song
import io.reactivex.Observable

interface ChildsHasSongs<in Param> {

    fun observeSongListByParam(param: Param): Observable<List<Song>>

}
