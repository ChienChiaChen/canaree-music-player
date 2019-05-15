package dev.olog.msc.data.db.most.played

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.olog.msc.data.entity.PlaylistMostPlayedEntity
import dev.olog.msc.data.entity.SongMostTimesPlayedEntity
import io.reactivex.Flowable

@Dao
internal abstract class PlaylistMostPlayedDao {

    @Query("""
        SELECT songId, count(*) as timesPlayed
        FROM most_played_playlist
        WHERE playlistId = :playlistId
        GROUP BY songId
        HAVING count(*) >= 5
        ORDER BY timesPlayed DESC
        LIMIT :limit
    """)
    internal abstract fun query(playlistId: Long, limit: Int): List<SongMostTimesPlayedEntity>

    @Query("""
        SELECT songId, count(*) as timesPlayed
        FROM most_played_playlist
        WHERE playlistId = :playlistId
        GROUP BY songId
        HAVING count(*) >= 5
        ORDER BY timesPlayed DESC
        LIMIT :limit
    """)
    internal abstract fun observe(playlistId: Long, limit: Int): Flowable<List<SongMostTimesPlayedEntity>>

    @Query("""
        SELECT count(*)
        FROM (
            SELECT songId, count(*) as timesPlayed
            FROM most_played_playlist
            WHERE playlistId = :playlistId
            GROUP BY songId
            HAVING count(*) >= 5
        )
    """)
    internal abstract fun count(playlistId: Long): Int

    @Insert
    internal abstract fun insertOne(item: PlaylistMostPlayedEntity)

}
