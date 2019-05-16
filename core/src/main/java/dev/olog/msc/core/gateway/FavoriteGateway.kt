package dev.olog.msc.core.gateway

import dev.olog.msc.core.entity.favorite.FavoriteEnum
import dev.olog.msc.core.entity.favorite.FavoriteStateEntity
import dev.olog.msc.core.entity.favorite.FavoriteType
import io.reactivex.Flowable
import io.reactivex.Observable

interface FavoriteGateway {

    fun getAll(limit: Int, offset: Int): List<Long>
    fun getAllPodcasts(limit: Int, offset: Int): List<Long>

    fun observeAll(): Flowable<List<Long>>
    fun observeAllPodcast(): Flowable<List<Long>>

    fun countAll(): Int
    fun countAllPodcast(): Int

    suspend fun addSingle(type: FavoriteType, songId: Long)
    suspend fun addGroup(type: FavoriteType, songListId: List<Long>)

    suspend fun deleteSingle(type: FavoriteType, songId: Long)
    suspend fun deleteGroup(type: FavoriteType, songListId: List<Long>)

    suspend fun deleteAll(type: FavoriteType)

    suspend fun isFavorite(type: FavoriteType, songId: Long): Boolean

    fun observeToggleFavorite(): Observable<FavoriteEnum>
    fun updateFavoriteState(type: FavoriteType, state: FavoriteStateEntity)

    suspend fun toggleFavorite()

}