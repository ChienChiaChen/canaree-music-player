package dev.olog.msc.core.gateway

import dev.olog.msc.core.entity.Podcast
import io.reactivex.Observable

interface ChildsHasPodcasts<in Param> {

    fun observePodcastListByParam(param: Param): Observable<List<Podcast>>

}
