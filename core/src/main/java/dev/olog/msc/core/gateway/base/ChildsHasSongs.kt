package dev.olog.msc.core.gateway.base

import dev.olog.msc.core.entity.data.request.DataRequest
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.track.Song

interface ChildsHasSongs<in Param> {

    fun getSongListByParam(param: Param): DataRequest<Song>
    fun getSongListByParamDuration(param: Param, filter: Filter): Int

}
