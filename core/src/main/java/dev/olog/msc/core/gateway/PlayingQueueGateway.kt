package dev.olog.msc.core.gateway

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.PlayingQueueSong
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow

interface PlayingQueueGateway {

    companion object {
        const val MINI_QUEUE_SIZE = 50
    }

    suspend fun observeAll(): Flow<List<PlayingQueueSong>>
    suspend fun getAll(): List<PlayingQueueSong>

    // mediaId, trackId, idInPlaylist
    suspend fun update(list: List<Triple<MediaId, Long, Int>>)

    fun observeMiniQueue(): Observable<List<PlayingQueueSong>>
    suspend fun updateMiniQueue(tracksId: List<Pair<Int, Long>>)

}