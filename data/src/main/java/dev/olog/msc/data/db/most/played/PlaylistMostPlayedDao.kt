package dev.olog.msc.data.db.most.played

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.data.entity.PlaylistMostPlayedEntity
import dev.olog.msc.data.entity.SongMostTimesPlayedEntity
import dev.olog.msc.shared.extensions.mapToList
import io.reactivex.Flowable
import io.reactivex.Observable

@Dao
internal abstract class PlaylistMostPlayedDao {

    @Query("""
        SELECT songId, count(*) as timesPlayed
        FROM most_played_playlist
        WHERE playlistId = :playlistId
        GROUP BY songId
        HAVING count(*) >= 5
        ORDER BY timesPlayed DESC
        LIMIT 10
    """)
    internal abstract fun query(playlistId: Long): Flowable<List<SongMostTimesPlayedEntity>>

    @Insert
    internal abstract fun insertOne(item: PlaylistMostPlayedEntity)

    internal fun getAll(playlistId: Long, songList: Observable<List<Song>>): Observable<List<Song>> {
        return this.query(playlistId)
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
