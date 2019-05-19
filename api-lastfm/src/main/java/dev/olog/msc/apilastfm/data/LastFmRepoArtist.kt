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
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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
            return cacheAsync(artistId, artistInfo).await().toDomain()
        } catch (ex: Exception) {
            return null
        }
    }

    private suspend fun cacheAsync(artistId: Long, model: ArtistInfo): Deferred<LastFmArtistEntity> =
        GlobalScope.async {
            assertBackgroundThread()
            val entity = model.toModel(artistId)
            dao.insertArtist(entity)
            entity
        }

    private suspend fun cacheEmptyAsync(artistId: Long): Deferred<LastFmArtistEntity> = GlobalScope.async {
        assertBackgroundThread()
        val entity = LastFmNulls.createNullArtist(artistId)
        dao.insertArtist(entity)
        entity
    }

    suspend fun delete(artistId: Long) = GlobalScope.launch {
        assertBackgroundThread()
        dao.deleteArtist(artistId)
    }

}