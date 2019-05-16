package dev.olog.msc.data.db.last.played

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import dev.olog.msc.data.entity.LastPlayedPodcastAlbumEntity
import io.reactivex.Flowable

@Dao
internal abstract class LastPlayedPodcastAlbumDao {

    @Query(
        """
        SELECT * FROM last_played_podcast_albums
        ORDER BY dateAdded DESC
        LIMIT :limit
    """
    )
    internal abstract fun getAll(limit: Int): List<LastPlayedPodcastAlbumEntity>

    @Query(
        """
        SELECT * FROM last_played_podcast_albums
        ORDER BY dateAdded DESC
        LIMIT :limit
    """
    )
    internal abstract fun observeAll(limit: Int): Flowable<List<LastPlayedPodcastAlbumEntity>>

    @Query(
        """
        SELECT count(*) FROM last_played_podcast_albums
    """
    )
    internal abstract fun getCount(): Int

    @Insert
    internal abstract suspend fun insertImpl(entity: LastPlayedPodcastAlbumEntity)

    @Query(
        """
        DELETE FROM last_played_podcast_albums
        WHERE id = :albumId
    """
    )
    internal abstract suspend fun deleteImpl(albumId: Long)

    @Transaction
    internal open suspend fun insertOne(id: Long) {
        deleteImpl(id)
        insertImpl(LastPlayedPodcastAlbumEntity(id))
    }

}