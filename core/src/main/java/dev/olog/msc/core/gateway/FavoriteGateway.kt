package dev.olog.msc.core.gateway

import dev.olog.msc.core.entity.favorite.FavoriteEnum
import dev.olog.msc.core.entity.favorite.FavoriteStateEntity
import dev.olog.msc.core.entity.favorite.FavoriteType
import kotlinx.coroutines.flow.Flow

interface FavoriteGateway {

    fun getAll(limit: Int, offset: Int): List<Long>
    fun getAllPodcasts(limit: Int, offset: Int): List<Long>

    fun observeAll(): Flow<List<Long>>
    fun observeAllPodcast(): Flow<List<Long>>

    fun countAll(): Int
    fun countAllPodcast(): Int

    suspend fun addSingle(type: FavoriteType, songId: Long)

    suspend fun deleteSingle(type: FavoriteType, songId: Long)
    suspend fun deleteGroup(type: FavoriteType, songListId: List<Long>)

    suspend fun deleteAll(type: FavoriteType)

    suspend fun isFavorite(type: FavoriteType, songId: Long): Boolean

    suspend fun observeToggleFavorite(): Flow<FavoriteEnum>
    fun updateFavoriteState(type: FavoriteType, state: FavoriteStateEntity)

    suspend fun toggleFavorite()

}