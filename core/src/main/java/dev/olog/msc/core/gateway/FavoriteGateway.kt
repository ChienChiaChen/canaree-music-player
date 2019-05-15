package dev.olog.msc.core.gateway

import dev.olog.msc.core.entity.favorite.FavoriteEnum
import dev.olog.msc.core.entity.favorite.FavoriteStateEntity
import dev.olog.msc.core.entity.favorite.FavoriteType
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

interface FavoriteGateway {

    fun getAll(limit: Int, offset: Int): List<Long>
    fun getAllPodcasts(limit: Int, offset: Int): List<Long>

    fun observeAll(): Flowable<List<Long>>
    fun observeAllPodcast(): Flowable<List<Long>>

    fun countAll(): Int
    fun countAllPodcast(): Int

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