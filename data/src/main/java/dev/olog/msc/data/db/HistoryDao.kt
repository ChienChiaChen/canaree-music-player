package dev.olog.msc.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.msc.data.entity.HistoryEntity
import dev.olog.msc.data.entity.PodcastHistoryEntity
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
internal abstract class HistoryDao {


    @Query("""
        SELECT * FROM song_history
        ORDER BY dateAdded DESC
        LIMIT :limit
        OFFSET :offset
    """)
    internal abstract fun getAll(limit: Int, offset: Int): List<HistoryEntity>

    @Query("""
        SELECT * FROM podcast_song_history
        ORDER BY dateAdded DESC
        LIMIT :limit
        OFFSET :offset
    """)
    internal abstract fun getAllPodcasts(limit: Int, offset: Int): List<PodcastHistoryEntity>

    @Query(
        """
        SELECT *
        FROM song_history
    """
    )
    internal abstract fun observeAll(): Flowable<List<HistoryEntity>>

    @Query(
        """
        SELECT *
        FROM podcast_song_history
    """
    )
    internal abstract fun observeAllPodcast(): Flowable<List<PodcastHistoryEntity>>

    @Query("""SELECT count(*) FROM song_history""")
    internal abstract fun countAll(): Int

    @Query("""SELECT count(*) FROM podcast_song_history""")
    internal abstract fun countAllPodcast(): Int

    @Query("""DELETE FROM song_history""")
    internal abstract fun deleteAll()

    @Query("""DELETE FROM podcast_song_history""")
    internal abstract fun deleteAllPodcasts()

    @Query("""
        DELETE FROM song_history
        WHERE id = :songId
    """)
    internal abstract fun deleteSingle(songId: Long)

    @Query("""
        DELETE FROM podcast_song_history
        WHERE id = :podcastId
    """)
    internal abstract fun deleteSinglePodcast(podcastId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertImpl(entity: HistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertPodcastImpl(entity: PodcastHistoryEntity)

    internal fun insert(id: Long): Completable {
        return Completable.fromCallable{ insertImpl(HistoryEntity(songId = id)) }
    }

    internal fun insertPodcasts(id: Long): Completable {
        return Completable.fromCallable{ insertPodcastImpl(PodcastHistoryEntity(podcastId = id)) }
    }

}
