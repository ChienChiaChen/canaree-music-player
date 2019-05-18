package dev.olog.msc.core.gateway.base

import dev.olog.msc.core.entity.data.request.DataRequest
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.podcast.Podcast

interface ChildsHasPodcasts<in Param> {

    fun getPodcastListByParam(param: Param): DataRequest<Podcast>
    fun getPodcastListByParamDuration(param: Param, filter: Filter): Int

}
