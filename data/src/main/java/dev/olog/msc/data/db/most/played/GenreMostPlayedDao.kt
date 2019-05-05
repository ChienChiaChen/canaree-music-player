package dev.olog.msc.data.db.most.played

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.data.entity.GenreMostPlayedEntity
import dev.olog.msc.data.entity.SongMostTimesPlayedEntity
import dev.olog.msc.shared.extensions.mapToList
import io.reactivex.Flowable
import io.reactivex.Observable

@Dao
internal abstract class GenreMostPlayedDao {

    @Query("""
        SELECT songId, count(*) as timesPlayed
        FROM most_played_genre
        WHERE genreId = :genreId
        GROUP BY songId
        HAVING count(*) >= 5
        ORDER BY timesPlayed DESC
        LIMIT 10
    """)
    internal abstract fun query(genreId: Long): Flowable<List<SongMostTimesPlayedEntity>>

    @Insert
    internal abstract fun insertOne(item: GenreMostPlayedEntity)

    internal fun getAll(genreId: Long, songList: Observable<List<Song>>): Observable<List<Song>> {
        return this.query(genreId)
                .toObservable()
                .switchMap { mostPlayedSongs -> songList.map { songList ->
                    mostPlayedSongs.mapNotNull { mostPlayed ->
                        val song = songList.firstOrNull { it.id == mostPlayed.songId }
                        if (song != null) song to mostPlayed.timesPlayed
                        else null
                    }.sortedWith(compareByDescending { it.second })
                }.mapToList { it.first }
                }
    }

}
