package dev.olog.msc.core.gateway.base

import dev.olog.msc.core.entity.ChunkedData
import dev.olog.msc.core.entity.track.Song
import io.reactivex.Observable

interface ChildsHasSongs<in Param> {

    fun observeSongListByParam(param: Param): Observable<List<Song>>
    fun getSongListByParamChunk(param: Param): ChunkedData<Song>
    fun getSongListByParamDuration(param: Param): Int

}
