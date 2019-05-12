package dev.olog.msc.core.gateway

import dev.olog.msc.core.entity.favorite.FavoriteEnum
import dev.olog.msc.core.entity.favorite.FavoriteStateEntity
import dev.olog.msc.core.entity.favorite.FavoriteType
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.track.Song
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

interface FavoriteGateway {

    suspend fun getAll(): Flow<List<Song>>
    fun getAllPodcasts(): Observable<List<Podcast>>

    fun addSingle(type: FavoriteType, songId: Long): Completable
    fun addGroup(type: FavoriteType, songListId: List<Long>): Completable

    fun deleteSingle(type: FavoriteType, songId: Long): Completable
    fun deleteGroup(type: FavoriteType, songListId: List<Long>): Completable

    fun deleteAll(type: FavoriteType): Completable

    fun isFavorite(type: FavoriteType, songId: Long): Single<Boolean>

    fun observeToggleFavorite(): Observable<FavoriteEnum>
    fun updateFavoriteState(type: FavoriteType, state: FavoriteStateEntity)

    fun toggleFavorite()

}