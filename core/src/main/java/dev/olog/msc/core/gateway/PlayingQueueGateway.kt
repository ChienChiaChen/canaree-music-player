package dev.olog.msc.core.gateway

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.PlayingQueueSong
import dev.olog.msc.core.entity.data.request.Page
import kotlinx.coroutines.flow.Flow

interface PlayingQueueGateway {

    companion object {
        const val MINI_QUEUE_SIZE = 50
    }

    suspend fun observeAll(page: Page): Flow<List<PlayingQueueSong>>
    fun getAll(page: Page): List<PlayingQueueSong>
    fun getCount(): Int

    // mediaId, trackId, idInPlaylist
    suspend fun update(list: List<Triple<MediaId, Long, Int>>)

    fun isEmpty(): Flow<Boolean>

}