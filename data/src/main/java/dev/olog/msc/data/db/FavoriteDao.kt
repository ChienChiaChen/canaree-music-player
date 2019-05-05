package dev.olog.msc.data.db

import androidx.room.*
import dev.olog.msc.core.entity.favorite.FavoriteType
import dev.olog.msc.data.entity.FavoriteEntity
import dev.olog.msc.data.entity.FavoritePodcastEntity
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
internal abstract class FavoriteDao {

    @Query("SELECT songId FROM favorite_songs")
    internal abstract fun getAllImpl(): Flowable<List<Long>>

    @Query("SELECT podcastId FROM favorite_podcast_songs")
    internal abstract fun getAllPodcastsImpl(): Flowable<List<Long>>

    @Query("DELETE FROM favorite_songs")
    internal abstract fun deleteAll()

    @Query("DELETE FROM favorite_podcast_songs")
    internal abstract fun deleteAllPodcasts()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertOneImpl(item: FavoriteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertOnePodcastImpl(item: FavoritePodcastEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertGroupImpl(item: List<FavoriteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertGroupPodcastImpl(item: List<FavoritePodcastEntity>)

    @Delete
    internal abstract fun deleteGroupImpl(item: List<FavoriteEntity>)

    @Delete
    internal abstract fun deleteGroupPodcastImpl(item: List<FavoritePodcastEntity>)

    internal fun addToFavoriteSingle(type: FavoriteType, id: Long): Completable {
        return Completable.fromCallable {
            if (type == FavoriteType.TRACK) {
                insertOneImpl(FavoriteEntity(id))
            } else {
                insertOnePodcastImpl(FavoritePodcastEntity(id))
            }
        }
    }

    internal fun addToFavorite(type: FavoriteType, songIds: List<Long>): Completable {
        return Completable.fromCallable {
            if (type == FavoriteType.TRACK) {
                insertGroupImpl(songIds.map { FavoriteEntity(it) })
            } else {
                insertGroupPodcastImpl(songIds.map { FavoritePodcastEntity(it) })
            }
        }
    }

    internal open fun removeFromFavorite(type: FavoriteType, songId: List<Long>): Completable {
        return Completable.fromCallable {
            if (type == FavoriteType.TRACK){
                deleteGroupImpl(songId.map { FavoriteEntity(it) })
            } else {
                deleteGroupPodcastImpl(songId.map { FavoritePodcastEntity(it) })
            }
        }
    }

    @Query("SELECT songId FROM favorite_songs WHERE songId = :songId")
    internal abstract fun isFavorite(songId: Long): FavoriteEntity?

    @Query("SELECT podcastId FROM favorite_podcast_songs WHERE podcastId = :podcastId")
    internal abstract fun isFavoritePodcast(podcastId: Long): FavoritePodcastEntity?

}