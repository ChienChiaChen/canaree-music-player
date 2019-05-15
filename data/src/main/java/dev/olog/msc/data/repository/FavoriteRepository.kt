package dev.olog.msc.data.repository

import android.annotation.SuppressLint
import dev.olog.msc.core.entity.favorite.FavoriteEnum
import dev.olog.msc.core.entity.favorite.FavoriteStateEntity
import dev.olog.msc.core.entity.favorite.FavoriteType
import dev.olog.msc.core.gateway.FavoriteGateway
import dev.olog.msc.data.db.AppDatabase
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

internal class FavoriteRepository @Inject constructor(
    appDatabase: AppDatabase

) : FavoriteGateway {

    private val favoriteDao = appDatabase.favoriteDao()

    private val favoriteStatePublisher = BehaviorSubject.create<FavoriteStateEntity>()

    override fun observeToggleFavorite(): Observable<FavoriteEnum> = favoriteStatePublisher.map { it.enum }

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

    override fun addSingle(type: FavoriteType, songId: Long): Completable {
        return favoriteDao.addToFavoriteSingle(type, songId)
            .andThen {
                val id = favoriteStatePublisher.value?.songId ?: return@andThen
                if (songId == id) {
                    updateFavoriteState(type, FavoriteStateEntity(songId, FavoriteEnum.FAVORITE, type))
                }
                it.onComplete()
            }
    }

    override fun addGroup(type: FavoriteType, songListId: List<Long>): Completable {
        return favoriteDao.addToFavorite(type, songListId)
            .andThen {
                val songId = favoriteStatePublisher.value?.songId ?: return@andThen
                if (songListId.contains(songId)) {
                    updateFavoriteState(type, FavoriteStateEntity(songId, FavoriteEnum.FAVORITE, type))
                }
                it.onComplete()
            }
    }

    override fun deleteSingle(type: FavoriteType, songId: Long): Completable {
        return favoriteDao.removeFromFavorite(type, listOf(songId))
            .andThen {
                val id = favoriteStatePublisher.value?.songId ?: return@andThen
                if (songId == id) {
                    updateFavoriteState(type, FavoriteStateEntity(songId, FavoriteEnum.NOT_FAVORITE, type))
                }
                it.onComplete()
            }
    }

    override fun deleteGroup(type: FavoriteType, songListId: List<Long>): Completable {
        return favoriteDao.removeFromFavorite(type, songListId)
            .andThen {
                val songId = favoriteStatePublisher.value?.songId ?: return@andThen
                if (songListId.contains(songId)) {
                    updateFavoriteState(type, FavoriteStateEntity(songId, FavoriteEnum.NOT_FAVORITE, type))
                }
                it.onComplete()
            }
    }

    override fun deleteAll(type: FavoriteType): Completable {
        return Completable.fromCallable { favoriteDao.deleteAll() }
            .andThen {
                val songId = favoriteStatePublisher.value?.songId ?: return@andThen
                updateFavoriteState(type, FavoriteStateEntity(songId, FavoriteEnum.NOT_FAVORITE, type))
                it.onComplete()
            }
    }

    override fun isFavorite(type: FavoriteType, songId: Long): Single<Boolean> {
        return Single.fromCallable { favoriteDao.isFavorite(songId) != null }
    }

    // leaks for very small amount of time
    @SuppressLint("RxLeakedSubscription")
    override fun toggleFavorite() {
        val value = favoriteStatePublisher.value ?: return
        val id = value.songId
        val state = value.enum
        val type = value.favoriteType

        var action: Completable? = null

        when (state) {
            FavoriteEnum.NOT_FAVORITE -> {
                updateFavoriteState(type, FavoriteStateEntity(id, FavoriteEnum.ANIMATE_TO_FAVORITE, type))
                action = favoriteDao.addToFavoriteSingle(type, id)
            }
            FavoriteEnum.FAVORITE -> {
                updateFavoriteState(type, FavoriteStateEntity(id, FavoriteEnum.ANIMATE_NOT_FAVORITE, type))
                action = favoriteDao.removeFromFavorite(type, listOf(id))
            }
            else -> Completable.complete()
        }

        action?.subscribeOn(Schedulers.io())
            ?.subscribe({}, Throwable::printStackTrace)
    }
}