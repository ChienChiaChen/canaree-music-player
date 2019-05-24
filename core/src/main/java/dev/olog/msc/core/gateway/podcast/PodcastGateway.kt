package dev.olog.msc.core.gateway.podcast

import dev.olog.msc.core.entity.data.request.ItemRequest
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.gateway.base.BaseGateway
import kotlinx.coroutines.flow.Flow

interface PodcastGateway : BaseGateway<Podcast, Long> {

    fun getAllUnfiltered(): Flow<List<Podcast>>

    fun getByAlbumId(albumId: Long): ItemRequest<Podcast>

    fun deleteSingle(podcastId: Long)
    fun deleteGroup(podcastList: List<Podcast>)

    fun getUneditedByParam(podcastId: Long): Flow<Podcast>

    fun getCurrentPosition(podcastId: Long, duration: Long): Long
    fun saveCurrentPosition(podcastId: Long, position: Long)

}