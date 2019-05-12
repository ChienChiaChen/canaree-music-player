package dev.olog.msc.data.db.last.played

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.olog.msc.data.entity.LastPlayedArtistEntity
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
internal abstract class LastPlayedArtistDao {

    @Query("""
        SELECT * FROM last_played_artists
        ORDER BY dateAdded DESC
        LIMIT :limit
    """)
    internal abstract fun getAll(limit: Int): List<LastPlayedArtistEntity>

    @Query("""
        SELECT * FROM last_played_artists
        ORDER BY dateAdded DESC
        LIMIT :limit
    """)
    internal abstract fun observeAll(limit: Int): Flowable<List<LastPlayedArtistEntity>>

    @Query("""
        SELECT count(*) FROM last_played_artists
    """)
    internal abstract fun getCount(): Int

    @Insert
    internal abstract fun insertImpl(entity: LastPlayedArtistEntity)

    @Query("""
        DELETE FROM last_played_artists
        WHERE id = :artistId
    """)
    internal abstract fun deleteImpl(artistId: Long)

    internal fun insertOne(id: Long) : Completable {
        return Completable.fromCallable{ deleteImpl(id) }
                .andThen { insertImpl(LastPlayedArtistEntity(id)) }
    }

}
