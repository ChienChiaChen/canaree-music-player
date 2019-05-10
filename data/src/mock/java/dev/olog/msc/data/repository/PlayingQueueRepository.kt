package dev.olog.msc.data.repository

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.PlayingQueueSong
import dev.olog.msc.core.gateway.PlayingQueueGateway
import dev.olog.msc.core.gateway.PodcastGateway
import dev.olog.msc.core.gateway.SongGateway
import dev.olog.msc.data.db.AppDatabase
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

internal class PlayingQueueRepository @Inject constructor(
        database: AppDatabase,
        private val songGateway: SongGateway,
        private val podcastGateway: PodcastGateway

) : PlayingQueueGateway {

    private val playingQueueDao = database.playingQueueDao()

    override fun getAll(): Single<List<PlayingQueueSong>> {
        return Single.just(
            listOf(
                PlayingQueueSong(0, 0, MediaId.playingQueueId, 0, 0,
                    "title", "artist", "", "album", "", 0, 0, "", "",
                    0, 0, false)
            )
        )
    }

    override fun observeAll(): Observable<List<PlayingQueueSong>> {
        return getAll().toObservable()
    }

    override fun update(list: List<Triple<MediaId, Long, Int>>): Completable {
        return Completable.complete()
    }

    override fun observeMiniQueue(): Observable<List<PlayingQueueSong>> {
        return observeAll()
    }

    override fun updateMiniQueue(tracksId: List<Pair<Int, Long>>) {

    }

}
