package dev.olog.msc.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.PlayingQueueSong
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.data.entity.MiniQueueEntity
import dev.olog.msc.data.entity.PlayingQueueEntity
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import io.reactivex.schedulers.Schedulers

@Dao
abstract class PlayingQueueDao {

    @Query("""
        SELECT * FROM playing_queue
        ORDER BY progressive
    """)
    internal abstract fun getAllImpl(): List<PlayingQueueEntity>

    @Query("DELETE FROM playing_queue")
    internal abstract suspend fun deleteAllImpl()

    @Query("""
        SELECT *
        FROM mini_queue
        ORDER BY timeAdded
    """)
    internal abstract fun getMiniQueueImpl(): Flowable<List<MiniQueueEntity>>

    fun observeMiniQueue(songList: Single<List<Song>>, podcastList: Single<List<Podcast>>)
            : Observable<List<PlayingQueueSong>> {


        return getMiniQueueImpl()
                .subscribeOn(Schedulers.io())
                .toObservable()
                .flatMapSingle { ids ->  Singles.zip(songList, podcastList) { songList, podcastList ->
                    val result = mutableListOf<PlayingQueueSong>()
                    for (item in ids){
                        var song : Any? = songList.firstOrNull { it.id == item.id }
                        if (song == null){
                            song = podcastList.firstOrNull { it.id == item.id }
                        }
                        if (song == null){
                            continue
                        }

                        val itemToAdd = if (song is Song){
                            song.toPlayingQueueSong(item.idInPlaylist, MediaIdCategory.SONGS.toString(), "")
                        } else if (song is Podcast){
                            song.toPlayingQueueSong(item.idInPlaylist, MediaIdCategory.SONGS.toString(), "")
                        } else {
                            throw IllegalArgumentException("must be song or podcast, passed $song")
                        }
                        result.add(itemToAdd)

                    }
                    result.toList()

                } }
    }

    @Transaction
    open suspend fun updateMiniQueue(list: List<Pair<Int, Long>>) {
        deleteMiniQueueImpl()
        insertMiniQueueImpl(list.map { MiniQueueEntity(it.first, it.second, System.nanoTime()) })
    }

    @Insert
    internal abstract fun insertAllImpl(list: List<PlayingQueueEntity>)

    @Query("DELETE FROM mini_queue")
    internal abstract suspend fun deleteMiniQueueImpl()

    @Insert
    internal abstract suspend fun insertMiniQueueImpl(list: List<MiniQueueEntity>)

    suspend fun getAllAsSongs(songList: List<Song>, podcastList: List<Podcast>)
            : List<PlayingQueueSong> {

        // TODO use trackQueries.exists(..)

        val playingQueue = getAllImpl()
        val result = mutableListOf<PlayingQueueSong>()

        for (item in playingQueue) {
            var song : Any? = songList.firstOrNull { it.id == item.songId }
            if (song == null){
                song = podcastList.firstOrNull { it.id == item.songId }
            }
            if (song == null){
                continue
            }

            val itemToAdd = if (song is Song){
                song.toPlayingQueueSong(item.idInPlaylist, item.category, item.categoryValue)
            } else if (song is Podcast){
                song.toPlayingQueueSong(item.idInPlaylist, item.category, item.categoryValue)
            } else {
                throw IllegalArgumentException("must be song or podcast, passed $song")
            }
            result.add(itemToAdd)
        }
        return result
    }

//    @Transaction TODO
    suspend fun insert(list: List<Triple<MediaId, Long, Int>>) {
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
                this.image,
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
                this.image,
                this.duration,
                this.dateAdded,
                this.path,
                this.discNumber,
                this.trackNumber,
                true
        )
    }


}