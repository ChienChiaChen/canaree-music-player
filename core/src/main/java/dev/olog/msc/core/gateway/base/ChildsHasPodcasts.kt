package dev.olog.msc.core.gateway.base

import dev.olog.msc.core.entity.ChunkedData
import dev.olog.msc.core.entity.podcast.Podcast
import io.reactivex.Observable

interface ChildsHasPodcasts<in Param> {

    fun observePodcastListByParam(param: Param): Observable<List<Podcast>>
    fun getPodcastListByParamChunk(param: Param): ChunkedData<Podcast>
    fun getPodcastListByParamDuration(param: Param): Int

}
