package dev.olog.msc.core.gateway

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.PlayingQueueSong
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface PlayingQueueGateway {

    companion object {
        const val MINI_QUEUE_SIZE = 50
    }

    fun observeAll(): Observable<List<PlayingQueueSong>>

    fun getAll(): Single<List<PlayingQueueSong>>

    // mediaId, trackId, idInPlaylist
    fun update(list: List<Triple<MediaId, Long, Int>>): Completable

    fun observeMiniQueue(): Observable<List<PlayingQueueSong>>
    fun updateMiniQueue(tracksId: List<Pair<Int, Long>>)

}