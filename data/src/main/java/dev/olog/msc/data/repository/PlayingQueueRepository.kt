package dev.olog.msc.data.repository

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.PlayingQueueSong
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Page
import dev.olog.msc.core.entity.data.request.getAll
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.PlayingQueueGateway
import dev.olog.msc.core.gateway.podcast.PodcastGateway
import dev.olog.msc.core.gateway.track.SongGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.shared.utils.assertBackgroundThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

internal class PlayingQueueRepository @Inject constructor(
    database: AppDatabase,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway

) : PlayingQueueGateway {

    private val playingQueueDao = database.playingQueueDao()

    override fun getAll(page: Page): List<PlayingQueueSong> {
        assertBackgroundThread()

        val allSongs = songGateway.getAll().getAll(Filter.NO_FILTER)
        val playingQueue =
            playingQueueDao.getAllAsSongs(allSongs, podcastGateway.getAll().getAll(Filter.NO_FILTER), page)
        if (playingQueue.isNotEmpty()) {
            return playingQueue
        }
        return allSongs.mapIndexed { index, song -> song.toPlayingQueueSong(index) }
    }

    override fun getCount(): Int {
        assertBackgroundThread()
        return playingQueueDao.getCount()
    }

    override suspend fun update(list: List<Triple<MediaId, Long, Int>>) {
        assertBackgroundThread()
        playingQueueDao.insert(list)
    }

    override suspend fun observeAll(page: Page): Flow<List<PlayingQueueSong>> {
        return playingQueueDao
            .obsereveAllAsSongs(songGateway, podcastGateway, page)
            .distinctUntilChanged()
    }

    override fun isEmpty(): Flow<Boolean> {
        return playingQueueDao.observeCount()
            .asFlow()
            .map { it == 0 }
            .distinctUntilChanged()
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
            this.duration,
            this.dateAdded,
            this.path,
            this.discNumber,
            this.trackNumber,
            false
        )
    }

}
