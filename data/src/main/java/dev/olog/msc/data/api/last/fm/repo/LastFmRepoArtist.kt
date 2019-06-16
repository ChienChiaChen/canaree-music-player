package dev.olog.msc.data.api.last.fm.repo

import dev.olog.msc.core.entity.Artist
import dev.olog.msc.core.entity.LastFmArtist
import dev.olog.msc.core.gateway.ArtistGateway
import dev.olog.msc.data.api.last.fm.LastFmService
import dev.olog.msc.data.api.last.fm.annotation.Proxy
import dev.olog.msc.data.api.last.fm.artist.info.ArtistInfo
import dev.olog.msc.data.api.last.fm.mapper.LastFmNulls
import dev.olog.msc.data.api.last.fm.mapper.toDomain
import dev.olog.msc.data.api.last.fm.mapper.toModel
import dev.olog.msc.data.dao.AppDatabase
import dev.olog.msc.data.entity.LastFmArtistEntity
import kotlinx.coroutines.rx2.awaitFirst
import javax.inject.Inject

class LastFmRepoArtist @Inject constructor(
    appDatabase: AppDatabase,
    @Proxy private val lastFmService: LastFmService,
    private val artistGateway: ArtistGateway

) {

    private val dao = appDatabase.lastFmDao()

    suspend fun shouldFetch(artistId: Long): Boolean {
        return dao.getArtist(artistId) == null
    }

    suspend fun get(artistId: Long): LastFmArtist? {
        val cachedValue = getFromCache(artistId)
        if (cachedValue != null) {
            return cachedValue
        }
        val artist = artistGateway.getByParam(artistId).awaitFirst() ?: return null
        return fetch(artist)
    }

    private suspend fun getFromCache(artistId: Long): LastFmArtist? {
        return dao.getArtist(artistId)?.toDomain()
    }

    private suspend fun fetch(artist: Artist): LastFmArtist? {
        val artistId = artist.id

        try {
            val artistInfo = lastFmService.getArtistInfoAsync(artist.name).await()
            return cache(artistId, artistInfo).toDomain()
        } catch (ex: Exception) {
            cacheEmpty(artistId)
            return null
        }
    }

    private suspend fun cache(artistId: Long, model: ArtistInfo): LastFmArtistEntity {
        val entity = model.toModel(artistId)
        dao.insertArtist(entity)
        return entity
    }

    private suspend fun cacheEmpty(artistId: Long): LastFmArtistEntity {
        val entity = LastFmNulls.createNullArtist(artistId)
        dao.insertArtist(entity)
        return entity
    }

    suspend fun delete(artistId: Long) {
        dao.deleteArtist(artistId)
    }

}