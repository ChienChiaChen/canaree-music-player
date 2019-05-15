package dev.olog.msc.apilastfm.data

import com.github.dmstocking.optional.java.util.Optional
import dev.olog.msc.apilastfm.LastFmService
import dev.olog.msc.apilastfm.annotation.Proxy
import dev.olog.msc.apilastfm.artist.info.ArtistInfo
import dev.olog.msc.apilastfm.mapper.LastFmNulls
import dev.olog.msc.apilastfm.mapper.toDomain
import dev.olog.msc.apilastfm.mapper.toModel
import dev.olog.msc.core.entity.LastFmArtist
import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.gateway.track.ArtistGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.LastFmArtistEntity
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

internal class LastFmRepoArtist @Inject constructor(
        appDatabase: AppDatabase,
        @Proxy private val lastFmService: LastFmService,
        private val artistGateway: ArtistGateway

) {

    private val dao = appDatabase.lastFmDao()

    fun shouldFetch(artistId: Long): Single<Boolean> {
        return Single.fromCallable { dao.getArtist(artistId) == null }
                .subscribeOn(Schedulers.io())
    }

    fun get(artistId: Long): Single<Optional<LastFmArtist?>> = runBlocking{
        val cachedValue = getFromCache(artistId)

        val fetch = Single.just(artistGateway.getByParam(artistId))
                .flatMap { fetch(it) }
                .map { Optional.of(it) }

        cachedValue.onErrorResumeNext(fetch)
                .subscribeOn(Schedulers.io())
    }

    private fun getFromCache(artistId: Long): Single<Optional<LastFmArtist?>> {
        return Single.fromCallable { Optional.ofNullable(dao.getArtist(artistId)) }
                .map {
                    if (it.isPresent){
                        Optional.of(it.get()!!.toDomain())
                    } else throw NoSuchElementException()
                }
    }

    private fun fetch(artist: Artist): Single<LastFmArtist> {
        val artistId = artist.id

        return lastFmService.getArtistInfo(artist.name)
                .map {
                    try {
                        cache(artistId, it)
                        val model = it.toModel(artistId)
                        dao.insertArtist(model)
                        it.toDomain(artistId)
                    } catch (ex: NoSuchElementException){
                        cacheEmpty(artistId)
                        throw ex
                    }
                }
    }

    private fun cache(artistId: Long, model: ArtistInfo): LastFmArtistEntity{
        val entity = model.toModel(artistId)
        dao.insertArtist(entity)
        return entity
    }

    private fun cacheEmpty(artistId: Long): LastFmArtistEntity{
        val entity = LastFmNulls.createNullArtist(artistId)
        dao.insertArtist(entity)
        return entity
    }

    fun delete(artistId: Long) {
        dao.deleteArtist(artistId)
    }

}