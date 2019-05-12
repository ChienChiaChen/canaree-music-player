package dev.olog.msc.data.db.last.played

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.olog.msc.data.entity.LastPlayedPodcastAlbumEntity
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
internal abstract class LastPlayedPodcastAlbumDao {

    @Query("""
        SELECT * FROM last_played_podcast_albums
        ORDER BY dateAdded DESC
        LIMIT :limit
    """)
    internal abstract fun getAll(limit: Int): List<LastPlayedPodcastAlbumEntity>

    @Query("""
        SELECT * FROM last_played_podcast_albums
        ORDER BY dateAdded DESC
        LIMIT :limit
    """)
    internal abstract fun observeAll(limit: Int): Flowable<List<LastPlayedPodcastAlbumEntity>>

    @Query("""
        SELECT count(*) FROM last_played_podcast_albums
    """)
    internal abstract fun getCount(): Int

    @Insert
    internal abstract fun insertImpl(entity: LastPlayedPodcastAlbumEntity)

    @Query("""
        DELETE FROM last_played_podcast_albums
        WHERE id = :albumId
    """)
    internal abstract fun deleteImpl(albumId: Long)

    internal fun insertOne(id: Long) : Completable {
        return Completable.fromCallable{ deleteImpl(id) }
                .andThen { insertImpl(LastPlayedPodcastAlbumEntity(id)) }
    }

}