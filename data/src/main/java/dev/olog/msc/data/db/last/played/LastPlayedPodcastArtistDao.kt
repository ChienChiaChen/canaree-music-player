package dev.olog.msc.data.db.last.played

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import dev.olog.msc.data.entity.LastPlayedPodcastArtistEntity
import io.reactivex.Flowable

@Dao
internal abstract class LastPlayedPodcastArtistDao {

    @Query(
        """
        SELECT * FROM last_played_podcast_artists
        ORDER BY dateAdded DESC
        LIMIT :limit
    """
    )
    internal abstract fun getAll(limit: Int): List<LastPlayedPodcastArtistEntity>

    @Query(
        """
        SELECT * FROM last_played_podcast_artists
        ORDER BY dateAdded DESC
        LIMIT :limit
    """
    )
    internal abstract fun observeAll(limit: Int): Flowable<List<LastPlayedPodcastArtistEntity>>

    @Query(
        """
        SELECT count(*) FROM last_played_podcast_artists
    """
    )
    internal abstract fun getCount(): Int

    @Insert
    internal abstract suspend fun insertImpl(entity: LastPlayedPodcastArtistEntity)

    @Query(
        """
        DELETE FROM last_played_podcast_artists
        WHERE id = :artistId
    """
    )
    internal abstract suspend fun deleteImpl(artistId: Long)

    @Transaction
    internal open suspend fun insertOne(id: Long) {
        deleteImpl(id)
        insertImpl(LastPlayedPodcastArtistEntity(id))
    }

}
