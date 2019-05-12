package dev.olog.msc.data.db.last.played

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.olog.msc.data.entity.LastPlayedAlbumEntity
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
internal abstract class LastPlayedAlbumDao {

    @Query("""
        SELECT * FROM last_played_albums
        ORDER BY dateAdded DESC
        LIMIT :limit
    """)
    internal abstract fun getAll(limit: Int): List<LastPlayedAlbumEntity>

    @Query("""
        SELECT * FROM last_played_albums
        ORDER BY dateAdded DESC
        LIMIT :limit
    """)
    internal abstract fun observeAll(limit: Int): Flowable<List<LastPlayedAlbumEntity>>

    @Query("""
        SELECT count(*) FROM last_played_albums
    """)
    internal abstract fun getCount(): Int

    @Insert
    internal abstract fun insertImpl(entity: LastPlayedAlbumEntity)

    @Query("""
        DELETE FROM last_played_albums
        WHERE id = :albumId
    """)
    internal abstract fun deleteImpl(albumId: Long)

    internal fun insertOne(id: Long) : Completable {
        return Completable.fromCallable{ deleteImpl(id) }
                .andThen { insertImpl(LastPlayedAlbumEntity(id)) }
    }

}