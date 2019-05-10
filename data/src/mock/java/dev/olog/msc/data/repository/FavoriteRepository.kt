package dev.olog.msc.data.repository

import dev.olog.msc.core.entity.favorite.FavoriteEnum
import dev.olog.msc.core.entity.favorite.FavoriteStateEntity
import dev.olog.msc.core.entity.favorite.FavoriteType
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.FavoriteGateway
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

internal class FavoriteRepository @Inject constructor() : FavoriteGateway {

    override fun observeToggleFavorite(): Observable<FavoriteEnum> = Observable.just(FavoriteEnum.NOT_FAVORITE)

    override fun updateFavoriteState(type: FavoriteType, state: FavoriteStateEntity) {
        
    }

    override fun getAll(): Observable<List<Song>> {
        return Observable.just(listOf())
    }

    override fun getAllPodcasts(): Observable<List<Podcast>> {
        return Observable.just(listOf())
    }

    override fun addSingle(type: FavoriteType, songId: Long): Completable {
        return Completable.complete()
    }

    override fun addGroup(type: FavoriteType, songListId: List<Long>): Completable {
        return Completable.complete()
    }

    override fun deleteSingle(type: FavoriteType, songId: Long): Completable {
        return Completable.complete()
    }

    override fun deleteGroup(type: FavoriteType, songListId: List<Long>): Completable {
        return Completable.complete()
    }

    override fun deleteAll(type: FavoriteType): Completable {
        return Completable.complete()
    }

    override fun isFavorite(type: FavoriteType, songId: Long): Single<Boolean> {
        return Single.just(false)
    }
    
    override fun toggleFavorite() {
        
    }
}