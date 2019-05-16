package dev.olog.msc.data.db

import androidx.room.*
import dev.olog.msc.data.entity.HistoryEntity
import dev.olog.msc.data.entity.PodcastHistoryEntity
import io.reactivex.Flowable

@Dao
internal abstract class HistoryDao {


    @Query(
        """
        SELECT * FROM song_history
        ORDER BY dateAdded DESC
        LIMIT :limit
        OFFSET :offset
    """
    )
    internal abstract fun getAll(limit: Int, offset: Int): List<HistoryEntity>

    @Query(
        """
        SELECT * FROM podcast_song_history
        ORDER BY dateAdded DESC
        LIMIT :limit
        OFFSET :offset
    """
    )
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
    internal abstract suspend fun deleteAll()

    @Query("""DELETE FROM podcast_song_history""")
    internal abstract suspend fun deleteAllPodcasts()

    @Query(
        """
        DELETE FROM song_history
        WHERE id = :songId
    """
    )
    internal abstract suspend fun deleteSingle(songId: Long)

    @Query(
        """
        DELETE FROM podcast_song_history
        WHERE id = :podcastId
    """
    )
    internal abstract suspend fun deleteSinglePodcast(podcastId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract suspend fun insertImpl(entity: HistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract suspend fun insertPodcastImpl(entity: PodcastHistoryEntity)

    @Transaction
    internal open suspend fun insert(id: Long) {
        insertImpl(HistoryEntity(songId = id))
    }

    @Transaction
    internal open suspend fun insertPodcasts(id: Long) {
        insertPodcastImpl(PodcastHistoryEntity(podcastId = id))
    }

}
