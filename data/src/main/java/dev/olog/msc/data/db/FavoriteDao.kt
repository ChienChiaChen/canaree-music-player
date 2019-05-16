package dev.olog.msc.data.db

import androidx.room.*
import dev.olog.msc.core.entity.favorite.FavoriteType
import dev.olog.msc.data.entity.FavoriteEntity
import dev.olog.msc.data.entity.FavoritePodcastEntity
import io.reactivex.Flowable

@Dao
internal abstract class FavoriteDao {

    @Query(
        """
        SELECT songId
        FROM favorite_songs
        ORDER BY songId
        LIMIT :limit
        OFFSET :offset
    """
    )
    internal abstract fun getAll(limit: Int, offset: Int): List<Long>

    @Query(
        """
        SELECT podcastId
        FROM favorite_podcast_songs
        ORDER BY podcastId
        LIMIT :limit
        OFFSET :offset
    """
    )
    internal abstract fun getAllPodcast(limit: Int, offset: Int): List<Long>

    @Query(
        """
        SELECT songId
        FROM favorite_songs
    """
    )
    internal abstract fun observeAll(): Flowable<List<Long>>

    @Query(
        """
        SELECT podcastId
        FROM favorite_podcast_songs
    """
    )
    internal abstract fun observeAllPodcast(): Flowable<List<Long>>

    @Query("SELECT count(*) FROM favorite_songs")
    abstract fun countAll(): Int

    @Query("SELECT count(*) FROM favorite_podcast_songs")
    abstract fun countAllPodcast(): Int

    @Query("DELETE FROM favorite_songs")
    internal abstract suspend fun deleteAll()

    @Query("DELETE FROM favorite_podcast_songs")
    internal abstract suspend fun deleteAllPodcasts()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    internal abstract suspend fun insertOneImpl(item: FavoriteEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    internal abstract suspend fun insertOnePodcastImpl(item: FavoritePodcastEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    internal abstract suspend fun insertGroupImpl(item: List<FavoriteEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    internal abstract suspend fun insertGroupPodcastImpl(item: List<FavoritePodcastEntity>)

    @Delete
    internal abstract suspend fun deleteGroupImpl(item: List<FavoriteEntity>)

    @Delete
    internal abstract suspend fun deleteGroupPodcastImpl(item: List<FavoritePodcastEntity>)

    @Transaction
    internal open suspend fun addToFavoriteSingle(type: FavoriteType, id: Long) {
        if (type == FavoriteType.TRACK) {
            insertOneImpl(FavoriteEntity(id))
        } else {
            insertOnePodcastImpl(FavoritePodcastEntity(id))
        }
    }

    @Transaction
    internal open suspend fun addToFavorite(type: FavoriteType, songIds: List<Long>) {
        if (type == FavoriteType.TRACK) {
            insertGroupImpl(songIds.map { FavoriteEntity(it) })
        } else {
            insertGroupPodcastImpl(songIds.map { FavoritePodcastEntity(it) })
        }
    }

    @Transaction
    internal open suspend fun removeFromFavorite(type: FavoriteType, songId: List<Long>) {
        if (type == FavoriteType.TRACK) {
            deleteGroupImpl(songId.map { FavoriteEntity(it) })
        } else {
            deleteGroupPodcastImpl(songId.map { FavoritePodcastEntity(it) })
        }
    }

    @Query("SELECT songId FROM favorite_songs WHERE songId = :songId")
    internal abstract suspend fun isFavorite(songId: Long): FavoriteEntity?

    @Query("SELECT podcastId FROM favorite_podcast_songs WHERE podcastId = :podcastId")
    internal abstract suspend fun isFavoritePodcast(podcastId: Long): FavoritePodcastEntity?

}