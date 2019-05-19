package dev.olog.msc.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.msc.data.entity.UsedAlbumImageEntity
import dev.olog.msc.data.entity.UsedArtistImageEntity
import dev.olog.msc.data.entity.UsedTrackImageEntity

@Dao
internal abstract class UsedImageDao {

    // get by param

    @Query("SELECT image FROM used_image_track WHERE id = :id")
    internal abstract fun getImageForTrack(id: Long): String?

    @Query("SELECT image FROM used_image_album WHERE id = :id")
    internal abstract fun getImageForAlbum(id: Long): String?

    @Query("SELECT image FROM used_image_artist WHERE id = :id")
    internal abstract fun getImageForArtist(id: Long): String?

    // insert

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract suspend fun insertForTrack(entity: UsedTrackImageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract suspend fun insertForAlbum(entity: UsedAlbumImageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract suspend fun insertForArtist(entity: UsedArtistImageEntity)

    // delete

    @Query("DELETE FROM used_image_track WHERE id = :id")
    internal abstract suspend fun deleteForTrack(id: Long)

    @Query("DELETE FROM used_image_album WHERE id = :id")
    internal abstract suspend fun deleteForAlbum(id: Long)

    @Query("DELETE FROM used_image_artist WHERE id = :id")
    internal abstract suspend fun deleteForArtist(id: Long)

}