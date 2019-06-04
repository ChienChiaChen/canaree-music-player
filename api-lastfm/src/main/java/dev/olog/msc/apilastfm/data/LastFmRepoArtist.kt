package dev.olog.msc.apilastfm.data

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
import dev.olog.msc.shared.utils.assertBackgroundThread
import javax.inject.Inject

internal class LastFmRepoArtist @Inject constructor(
    appDatabase: AppDatabase,
    @Proxy private val lastFmService: LastFmService,
    private val artistGateway: ArtistGateway

) {

    private val dao = appDatabase.lastFmDao()

    suspend fun shouldFetch(artistId: Long): Boolean {
        assertBackgroundThread()
        return dao.getArtist(artistId) == null
    }

    suspend fun get(artistId: Long): LastFmArtist? {
        assertBackgroundThread()
        val cachedValue = getFromCache(artistId)
        if (cachedValue != null) {
            return cachedValue
        }
        val artist = artistGateway.getByParam(artistId).getItem() ?: return null
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
        assertBackgroundThread()
        val entity = model.toModel(artistId)
        dao.insertArtist(entity)
        return entity
    }

    private suspend fun cacheEmpty(artistId: Long): LastFmArtistEntity {
        assertBackgroundThread()
        val entity = LastFmNulls.createNullArtist(artistId)
        dao.insertArtist(entity)
        return entity
    }

    suspend fun delete(artistId: Long) {
        assertBackgroundThread()
        dao.deleteArtist(artistId)
    }

}