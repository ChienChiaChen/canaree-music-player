package dev.olog.msc.data.repository

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.PlayingQueueSong
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.PlayingQueueGateway
import dev.olog.msc.core.gateway.PodcastGateway
import dev.olog.msc.core.gateway.SongGateway
import dev.olog.msc.data.db.AppDatabase
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

internal class PlayingQueueRepository @Inject constructor(
        database: AppDatabase,
        private val songGateway: SongGateway,
        private val podcastGateway: PodcastGateway

) : PlayingQueueGateway {

    private val playingQueueDao = database.playingQueueDao()

    override fun getAll(): Single<List<PlayingQueueSong>> = runBlocking{
        Single.concat(
                playingQueueDao.getAllAsSongs(
                        songGateway.getAll().asObservable().firstOrError(),
                        podcastGateway.getAll().asObservable().firstOrError()
                ).firstOrError(),

                songGateway.getAll().asObservable().firstOrError()
                        .map { it.mapIndexed { index, song -> song.toPlayingQueueSong(index) } }
        ).filter { it.isNotEmpty() }.firstOrError()
    }

    override fun observeAll(): Observable<List<PlayingQueueSong>> = runBlocking{
        playingQueueDao.getAllAsSongs(
                songGateway.getAll().asObservable().firstOrError(),
                podcastGateway.getAll().asObservable().firstOrError()
        )
    }

    override fun update(list: List<Triple<MediaId, Long, Int>>): Completable {
        return playingQueueDao.insert(list)
    }

    override fun observeMiniQueue(): Observable<List<PlayingQueueSong>> = runBlocking{
        playingQueueDao.observeMiniQueue(
                songGateway.getAll().asObservable().firstOrError(),
                podcastGateway.getAll().asObservable().firstOrError()
        )
    }

    override fun updateMiniQueue(tracksId: List<Pair<Int, Long>>) {
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
                this.folder,
                this.discNumber,
                this.trackNumber,
                false
        )
    }

}
