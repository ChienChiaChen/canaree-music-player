package dev.olog.msc.core.gateway.base

import dev.olog.msc.core.entity.PageRequest
import dev.olog.msc.core.entity.track.Song

interface ChildsHasSongs<in Param> {

    fun getSongListByParam(param: Param): PageRequest<Song>
    fun getSongListByParamDuration(param: Param): Int

}
