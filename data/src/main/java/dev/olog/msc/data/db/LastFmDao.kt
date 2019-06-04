package dev.olog.msc.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.msc.data.entity.LastFmAlbumEntity
import dev.olog.msc.data.entity.LastFmArtistEntity
import dev.olog.msc.data.entity.LastFmTrackEntity

private const val ARTIST_CACHE_TIME = "-7 day"
private const val ALBUM_CACHE_TIME = "-2 week"

@Dao
abstract class LastFmDao {

    // track

    @Query("""
        SELECT * FROM last_fm_track
        WHERE id = :id
        AND added BETWEEN date('now', '$ALBUM_CACHE_TIME') AND date('now')
    """)
    abstract suspend fun getTrack(id: Long): LastFmTrackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertTrack(entity: LastFmTrackEntity): Long

    @Query("DELETE FROM last_fm_track WHERE id = :trackId")
    abstract suspend fun deleteTrack(trackId: Long)

    // album

    @Query("""
        SELECT * FROM last_fm_album
        WHERE id = :id
        AND added BETWEEN date('now', '$ALBUM_CACHE_TIME') AND date('now')
    """)
    abstract suspend fun getAlbum(id: Long): LastFmAlbumEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAlbum(entity: LastFmAlbumEntity): Long

    @Query("DELETE FROM last_fm_album WHERE id = :albumId")
    abstract suspend fun deleteAlbum(albumId: Long)

    // artist

    @Query("""
        SELECT * FROM last_fm_artist
        WHERE id = :id
        AND added BETWEEN date('now', '$ARTIST_CACHE_TIME') AND date('now')
    """)
    abstract suspend fun getArtist(id: Long): LastFmArtistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertArtist(entity: LastFmArtistEntity): Long

    @Query("DELETE FROM last_fm_artist WHERE id = :artistId")
    abstract suspend fun deleteArtist(artistId: Long)
}