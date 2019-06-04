package dev.olog.msc.apilastfm.data

import dev.olog.msc.apilastfm.LastFmService
import dev.olog.msc.apilastfm.annotation.Proxy
import dev.olog.msc.apilastfm.mapper.LastFmNulls
import dev.olog.msc.apilastfm.mapper.toDomain
import dev.olog.msc.apilastfm.mapper.toModel
import dev.olog.msc.core.entity.LastFmAlbum
import dev.olog.msc.core.entity.track.Album
import dev.olog.msc.core.gateway.track.AlbumGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.LastFmAlbumEntity
import dev.olog.msc.shared.utils.assertBackgroundThread
import kotlinx.coroutines.yield
import javax.inject.Inject

internal class LastFmRepoAlbum @Inject constructor(
    appDatabase: AppDatabase,
    @Proxy private val lastFmService: LastFmService,
    private val albumGateway: AlbumGateway

) {

    private val dao = appDatabase.lastFmDao()

    suspend fun shouldFetch(albumId: Long): Boolean {
        assertBackgroundThread()
        return dao.getAlbum(albumId) == null
    }

    suspend fun get(albumId: Long): LastFmAlbum? {
        assertBackgroundThread()

        val cachedValue = getFromCache(albumId)
        if (cachedValue != null){
            return cachedValue
        }

        val album = albumGateway.getByParam(albumId).getItem() ?: return null
        if (album.hasSameNameAsFolder){
            return null
        }
        return fetch(album)
    }

    private suspend fun getFromCache(albumId: Long): LastFmAlbum? {
        val album = dao.getAlbum(albumId)
        return album?.toDomain()
    }

    private suspend fun fetch(album: Album): LastFmAlbum {
        assertBackgroundThread()

        val albumId = album.id

        try {
            val albumInfo = lastFmService.getAlbumInfoAsync(album.title, album.artist).await().toDomain(albumId)
            return cache(albumInfo).toDomain()
        } catch (ex: Exception){
            try {
                var searchedAlbum = lastFmService.searchAlbumAsync(album.title).await().toDomain(albumId, album.artist)
                yield()
                try {
                    searchedAlbum = lastFmService.getAlbumInfoAsync(searchedAlbum.title, searchedAlbum.artist).await().toDomain(albumId)
                } catch (ignored: Exception){}
                return cache(searchedAlbum).toDomain()
            } catch (ex: Exception){
                return cacheEmpty(albumId).toDomain()
            }

        }
    }

    private suspend fun cache(model: LastFmAlbum): LastFmAlbumEntity {
        assertBackgroundThread()
        val entity = model.toModel()
        dao.insertAlbum(entity)
        return entity
    }

    private suspend fun cacheEmpty(albumId: Long): LastFmAlbumEntity {
        assertBackgroundThread()
        val entity = LastFmNulls.createNullAlbum(albumId)
        dao.insertAlbum(entity)
        return entity
    }

    suspend fun delete(albumId: Long) {
        assertBackgroundThread()
        dao.deleteAlbum(albumId)
    }

}