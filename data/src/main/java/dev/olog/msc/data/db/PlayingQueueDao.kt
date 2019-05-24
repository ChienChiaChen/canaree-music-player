package dev.olog.msc.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.PlayingQueueSong
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Page
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.podcast.PodcastGateway
import dev.olog.msc.core.gateway.track.SongGateway
import dev.olog.msc.data.entity.PlayingQueueEntity
import io.reactivex.Flowable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.flow.asFlow

@Dao
abstract class PlayingQueueDao {

    @Query(
        """
        SELECT * FROM playing_queue
        ORDER BY progressive
        LIMIT :limit
        OFFSET :offset
    """
    )
    internal abstract fun getAllImpl(offset: Int, limit: Int): List<PlayingQueueEntity>

    @Query(
        """
        SELECT count(*) FROM playing_queue
    """
    )
    internal abstract fun getCount(): Int

    @Query(
        """
        SELECT count(*) FROM playing_queue
    """
    )
    internal abstract fun observeCount(): Flowable<Int>

    @Query(
        """
        SELECT * FROM playing_queue
        ORDER BY progressive
        LIMIT :limit
        OFFSET :offset
    """
    )
    internal abstract fun observeAllImpl(offset: Int, limit: Int): Flowable<List<PlayingQueueEntity>>

    @Query("DELETE FROM playing_queue")
    internal abstract suspend fun deleteAllImpl()

    @Insert
    internal abstract fun insertAllImpl(list: List<PlayingQueueEntity>)

    internal fun obsereveAllAsSongs(songGateway: SongGateway, podcastGateway: PodcastGateway, page: Page)
            : Flow<List<PlayingQueueSong>> {

        // TODO use trackQueries.exists(..) ??
        return observeAllImpl(offset = page.offset, limit = page.limit)
            .asFlow()
            .map { playingQueueEntities ->
                val result = mutableListOf<PlayingQueueSong>()

                val songList = songGateway.getAll().getPage(Request(Page.NO_PAGING, Filter.NO_FILTER))
                val podcastList = podcastGateway.getAll().getPage(Request(Page.NO_PAGING, Filter.NO_FILTER))

                for (item in playingQueueEntities) {
                    var song: Any? = songList.firstOrNull { it.id == item.songId }
                    if (song == null) {
                        song = podcastList.firstOrNull { it.id == item.songId }
                    }
                    if (song == null) {
                        continue
                    }

                    val itemToAdd = if (song is Song) {
                        song.toPlayingQueueSong(item.idInPlaylist, item.category, item.categoryValue)
                    } else if (song is Podcast) {
                        song.toPlayingQueueSong(item.idInPlaylist, item.category, item.categoryValue)
                    } else {
                        throw IllegalArgumentException("must be song or podcast, passed $song")
                    }
                    result.add(itemToAdd)
                }

                result
            }
    }

    internal fun getAllAsSongs(songList: List<Song>, podcastList: List<Podcast>, page: Page)
            : List<PlayingQueueSong> {

        // TODO use trackQueries.exists(..) ??

        val playingQueue = getAllImpl(offset = page.offset, limit = page.limit)
        val result = mutableListOf<PlayingQueueSong>()

        for (item in playingQueue) {
            var song: Any? = songList.firstOrNull { it.id == item.songId }
            if (song == null) {
                song = podcastList.firstOrNull { it.id == item.songId }
            }
            if (song == null) {
                continue
            }

            val itemToAdd = if (song is Song) {
                song.toPlayingQueueSong(item.idInPlaylist, item.category, item.categoryValue)
            } else if (song is Podcast) {
                song.toPlayingQueueSong(item.idInPlaylist, item.category, item.categoryValue)
            } else {
                throw IllegalArgumentException("must be song or podcast, passed $song")
            }
            result.add(itemToAdd)
        }
        return result
    }

    @Transaction
    internal open suspend fun insert(list: List<Triple<MediaId, Long, Int>>) {
        deleteAllImpl()

        val toAdd = list.map {
            val (mediaId, songId, idInPlaylist) = it
            PlayingQueueEntity(
                songId = songId,
                category = mediaId.category.toString(),
                categoryValue = mediaId.categoryValue,
                idInPlaylist = idInPlaylist
            )
        }
        insertAllImpl(toAdd)
    }

    private fun Song.toPlayingQueueSong(idInPlaylist: Int, category: String, categoryValue: String)
            : PlayingQueueSong {

        return PlayingQueueSong(
            this.id,
            idInPlaylist,
            MediaId.createCategoryValue(MediaIdCategory.valueOf(category), categoryValue),
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

    private fun Podcast.toPlayingQueueSong(idInPlaylist: Int, category: String, categoryValue: String)
            : PlayingQueueSong {

        return PlayingQueueSong(
            this.id,
            idInPlaylist,
            MediaId.createCategoryValue(MediaIdCategory.valueOf(category), categoryValue),
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
            true
        )
    }

}