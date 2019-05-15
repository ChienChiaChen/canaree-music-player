package dev.olog.msc.data.db.most.played

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.olog.msc.data.entity.GenreMostPlayedEntity
import dev.olog.msc.data.entity.SongMostTimesPlayedEntity
import io.reactivex.Flowable

@Dao
internal abstract class GenreMostPlayedDao {

    @Query("""
        SELECT songId, count(*) as timesPlayed
        FROM most_played_genre
        WHERE genreId = :genreId
        GROUP BY songId
        HAVING count(*) >= 5
        ORDER BY timesPlayed DESC
        LIMIT :limit
    """)
    internal abstract fun query(genreId: Long, limit: Int): List<SongMostTimesPlayedEntity>

    @Query("""
        SELECT songId, count(*) as timesPlayed
        FROM most_played_genre
        WHERE genreId = :genreId
        GROUP BY songId
        HAVING count(*) >= 5
        ORDER BY timesPlayed DESC
        LIMIT :limit
    """)
    internal abstract fun observe(genreId: Long, limit: Int): Flowable<List<SongMostTimesPlayedEntity>>

    @Query("""
        SELECT count(*)
        FROM (
            SELECT songId, count(*) as timesPlayed
            FROM most_played_genre
            WHERE genreId = :genreId
            GROUP BY songId
            HAVING count(*) >= 5
        )
    """)
    internal abstract fun count(genreId: Long): Int

    @Insert
    internal abstract fun insertOne(item: GenreMostPlayedEntity)

}
