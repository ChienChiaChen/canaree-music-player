package dev.olog.msc.data.repository

import dev.olog.msc.core.entity.favorite.FavoriteEnum
import dev.olog.msc.core.entity.favorite.FavoriteStateEntity
import dev.olog.msc.core.entity.favorite.FavoriteType
import dev.olog.msc.core.gateway.FavoriteGateway
import dev.olog.msc.data.db.AppDatabase
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

internal class FavoriteRepository @Inject constructor(
    appDatabase: AppDatabase

) : FavoriteGateway {

    private val favoriteDao = appDatabase.favoriteDao()

    private val favoriteStatePublisher = BehaviorProcessor.create<FavoriteStateEntity>()

    override suspend fun observeToggleFavorite(): Flow<FavoriteEnum> = favoriteStatePublisher
        .onBackpressureLatest()
        .map { it.enum }.asFlow()

    override fun updateFavoriteState(type: FavoriteType, state: FavoriteStateEntity) {
        favoriteStatePublisher.onNext(state)
        if (state.enum == FavoriteEnum.ANIMATE_NOT_FAVORITE) {
            favoriteStatePublisher.onNext(FavoriteStateEntity(state.songId, FavoriteEnum.NOT_FAVORITE, type))
        } else if (state.enum == FavoriteEnum.ANIMATE_TO_FAVORITE) {
            favoriteStatePublisher.onNext(FavoriteStateEntity(state.songId, FavoriteEnum.FAVORITE, type))
        }
    }

    override fun getAll(limit: Int, offset: Int): List<Long> {
        return favoriteDao.getAll(limit, offset)
    }

    override fun getAllPodcasts(limit: Int, offset: Int): List<Long> {
        return favoriteDao.getAllPodcast(limit, offset)
    }

    override fun observeAll(): Flowable<List<Long>> {
        return favoriteDao.observeAll()
    }

    override fun observeAllPodcast(): Flowable<List<Long>> {
        return favoriteDao.observeAllPodcast()
    }

    override fun countAll(): Int {
        return favoriteDao.countAll()
    }

    override fun countAllPodcast(): Int {
        return favoriteDao.countAllPodcast()
    }

    override suspend fun addSingle(type: FavoriteType, songId: Long) {
        favoriteDao.addToFavoriteSingle(type, songId)
        val id = favoriteStatePublisher.value?.songId ?: return
        if (songId == id) {
            updateFavoriteState(type, FavoriteStateEntity(songId, FavoriteEnum.FAVORITE, type))
        }
    }

    override suspend fun addGroup(type: FavoriteType, songListId: List<Long>) {
        favoriteDao.addToFavorite(type, songListId)
        val songId = favoriteStatePublisher.value?.songId ?: return
        if (songListId.contains(songId)) {
            updateFavoriteState(type, FavoriteStateEntity(songId, FavoriteEnum.FAVORITE, type))
        }
    }

    override suspend fun deleteSingle(type: FavoriteType, songId: Long) {
        favoriteDao.removeFromFavorite(type, listOf(songId))
        val id = favoriteStatePublisher.value?.songId ?: return
        if (songId == id) {
            updateFavoriteState(type, FavoriteStateEntity(songId, FavoriteEnum.NOT_FAVORITE, type))
        }

    }

    override suspend fun deleteGroup(type: FavoriteType, songListId: List<Long>) {
        favoriteDao.removeFromFavorite(type, songListId)

        val songId = favoriteStatePublisher.value?.songId ?: return
        if (songListId.contains(songId)) {
            updateFavoriteState(type, FavoriteStateEntity(songId, FavoriteEnum.NOT_FAVORITE, type))
        }
    }

    override suspend fun deleteAll(type: FavoriteType) {
        favoriteDao.deleteAll()
        val songId = favoriteStatePublisher.value?.songId ?: return
        updateFavoriteState(type, FavoriteStateEntity(songId, FavoriteEnum.NOT_FAVORITE, type))
    }

    override suspend fun isFavorite(type: FavoriteType, songId: Long): Boolean {
        return favoriteDao.isFavorite(songId) != null
    }

    override suspend fun toggleFavorite() {
        val value = favoriteStatePublisher.value ?: return
        val id = value.songId
        val state = value.enum
        val type = value.favoriteType

        when (state) {
            FavoriteEnum.NOT_FAVORITE -> {
                updateFavoriteState(type, FavoriteStateEntity(id, FavoriteEnum.ANIMATE_TO_FAVORITE, type))
                favoriteDao.addToFavoriteSingle(type, id)
            }
            FavoriteEnum.FAVORITE -> {
                updateFavoriteState(type, FavoriteStateEntity(id, FavoriteEnum.ANIMATE_NOT_FAVORITE, type))
            }
            FavoriteEnum.ANIMATE_NOT_FAVORITE, FavoriteEnum.ANIMATE_TO_FAVORITE -> {
            }
        }
    }
}