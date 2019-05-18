package dev.olog.msc.data.repository

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.PlayingQueueSong
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.getAll
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.PlayingQueueGateway
import dev.olog.msc.core.gateway.podcast.PodcastGateway
import dev.olog.msc.core.gateway.track.SongGateway
import dev.olog.msc.data.db.AppDatabase
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

internal class PlayingQueueRepository @Inject constructor(
    database: AppDatabase,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway

) : PlayingQueueGateway {

    private val playingQueueDao = database.playingQueueDao()

    override suspend fun getAll(): List<PlayingQueueSong> {
        val allSongs = songGateway.getAll().getAll(Filter.NO_FILTER)
        val playingQueue = playingQueueDao.getAllAsSongs(allSongs, podcastGateway.getAll().getAll(Filter.NO_FILTER))
        if (playingQueue.isNotEmpty()){
            return playingQueue
        }
        return allSongs.mapIndexed { index, song -> song.toPlayingQueueSong(index) }
    }

    override suspend fun update(list: List<Triple<MediaId, Long, Int>>) {
        playingQueueDao.insert(list)
    }

    override fun observeMiniQueue(): Observable<List<PlayingQueueSong>> {
        return Observable.just(listOf()) // TODO
//        return playingQueueDao.observeMiniQueue(
//                songGateway.getAll().asObservable().firstOrError(),
//                podcastGateway.getAll().asObservable().firstOrError()
//        )
    }

    override suspend fun observeAll(): Flow<List<PlayingQueueSong>> {
        return flowOf(getAll()) // TODO
    }

    override suspend fun updateMiniQueue(tracksId: List<Pair<Int, Long>>) {
        playingQueueDao.updateMiniQueue(tracksId)
    }

    private fun Song.toPlayingQueueSong(progressive: Int): PlayingQueueSong {
        return PlayingQueueSong(
                this.id,
                progressive,
                MediaId.songId(this.id),
                this.artistId,
                this.albumId,
                this.title,
                this.artist,
                this.albumArtist,
                this.album,
                this.image,
                this.duration,
                this.dateAdded,
                this.path,
                this.discNumber,
                this.trackNumber,
                false
        )
    }

}
