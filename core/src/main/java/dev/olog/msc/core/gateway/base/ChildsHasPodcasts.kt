package dev.olog.msc.core.gateway.base

import dev.olog.msc.core.entity.PageRequest
import dev.olog.msc.core.entity.podcast.Podcast

interface ChildsHasPodcasts<in Param> {

    fun getPodcastListByParam(param: Param): PageRequest<Podcast>
    fun getPodcastListByParamDuration(param: Param): Int

}
