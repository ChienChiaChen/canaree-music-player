package dev.olog.msc.apilastfm.data

import com.github.dmstocking.optional.java.util.Optional
import dev.olog.msc.apilastfm.LastFmService
import dev.olog.msc.apilastfm.annotation.Proxy
import dev.olog.msc.apilastfm.mapper.LastFmNulls
import dev.olog.msc.apilastfm.mapper.toDomain
import dev.olog.msc.apilastfm.mapper.toModel
import dev.olog.msc.core.entity.LastFmAlbum
import dev.olog.msc.core.entity.track.Album
import dev.olog.msc.core.gateway.AlbumGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.LastFmAlbumEntity
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

internal class LastFmRepoAlbum @Inject constructor(
        appDatabase: AppDatabase,
        @Proxy private val lastFmService: LastFmService,
        private val albumGateway: AlbumGateway

) {

    private val dao = appDatabase.lastFmDao()

    fun shouldFetch(albumId: Long): Single<Boolean> {
        return Single.fromCallable { dao.getAlbum(albumId) == null }
    }

    fun get(albumId: Long): Single<Optional<LastFmAlbum?>> {
        val cachedValue = getFromCache(albumId)

        val fetch = albumGateway.getByParam(albumId)
                .firstOrError()
                .flatMap {
                    if (it.hasSameNameAsFolder){
                        Single.error(Exception("image not downloadable"))
                    } else {
                        Single.just(it)
                    }
                }
                .flatMap { fetch(it) }
                .map { Optional.of(it) }

        return cachedValue.onErrorResumeNext(fetch)
                .subscribeOn(Schedulers.io())
    }

    private fun getFromCache(albumId: Long): Single<Optional<LastFmAlbum?>> {
        return Single.fromCallable { Optional.ofNullable(dao.getAlbum(albumId)) }
                .map {
                    if (it.isPresent){
                        Optional.of(it.get()!!.toDomain())
                    } else throw NoSuchElementException()
                }
    }

    private fun fetch(album: Album): Single<LastFmAlbum> {
        val albumId = album.id

        return lastFmService.getAlbumInfo(album.title, album.artist)
                .map { it.toDomain(albumId) }
                .doOnSuccess { cache(it) }
                .onErrorResumeNext { lastFmService.searchAlbum(album.title)
                        .map { it.toDomain(albumId, album.artist) }
                        .flatMap { result -> lastFmService.getAlbumInfo(result.title, result.artist)
                                .map { it.toDomain(albumId) }
                                .onErrorReturnItem(result)
                        }
                        .doOnSuccess { cache(it) }
                        .onErrorResumeNext {
                            if (it is NoSuchElementException){
                                Single.fromCallable { cacheEmpty(albumId) }
                                        .map { it.toDomain() }
                            } else Single.error(it)
                        }
                }
    }

    private fun cache(model: LastFmAlbum): LastFmAlbumEntity {
        val entity = model.toModel()
        dao.insertAlbum(entity)
        return entity
    }

    private fun cacheEmpty(albumId: Long): LastFmAlbumEntity{
        val entity = LastFmNulls.createNullAlbum(albumId)
        dao.insertAlbum(entity)
        return entity
    }

    fun delete(albumId: Long) {
        dao.deleteAlbum(albumId)
    }

}