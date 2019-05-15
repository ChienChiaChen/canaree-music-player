package dev.olog.msc.data.db.most.played

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.olog.msc.data.entity.FolderMostPlayedEntity
import dev.olog.msc.data.entity.SongMostTimesPlayedEntity
import io.reactivex.Flowable

@Dao
internal abstract class FolderMostPlayedDao {

    @Query("""
        SELECT songId, count(*) as timesPlayed
        FROM most_played_folder
        WHERE folderPath = :folderPath
        GROUP BY songId
        HAVING count(*) >= 5
        ORDER BY timesPlayed DESC
        LIMIT :limit
    """)
    internal abstract fun query(folderPath: String, limit: Int): List<SongMostTimesPlayedEntity>

    @Query("""
        SELECT songId, count(*) as timesPlayed
        FROM most_played_folder
        WHERE folderPath = :folderPath
        GROUP BY songId
        HAVING count(*) >= 5
        ORDER BY timesPlayed DESC
        LIMIT :limit
    """)
    internal abstract fun observe(folderPath: String, limit: Int): Flowable<List<SongMostTimesPlayedEntity>>

    @Query("""
        SELECT count(*)
        FROM (
            SELECT songId, count(*) as timesPlayed
            FROM most_played_folder
            WHERE folderPath = :folderPath
            GROUP BY songId
            HAVING count(*) >= 5
        )
    """)
    internal abstract fun count(folderPath: String): Int

    @Insert
    internal abstract fun insertOne(item: FolderMostPlayedEntity)

}
