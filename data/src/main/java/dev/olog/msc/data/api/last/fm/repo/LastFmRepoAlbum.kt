package dev.olog.msc.data.api.last.fm.repo

import dev.olog.msc.core.entity.Album
import dev.olog.msc.core.entity.LastFmAlbum
import dev.olog.msc.core.gateway.AlbumGateway
import dev.olog.msc.data.api.last.fm.LastFmService
import dev.olog.msc.data.api.last.fm.annotation.Proxy
import dev.olog.msc.data.api.last.fm.mapper.LastFmNulls
import dev.olog.msc.data.api.last.fm.mapper.toDomain
import dev.olog.msc.data.api.last.fm.mapper.toModel
import dev.olog.msc.data.dao.AppDatabase
import dev.olog.msc.data.entity.LastFmAlbumEntity
import kotlinx.coroutines.rx2.awaitFirst
import kotlinx.coroutines.yield
import javax.inject.Inject

class LastFmRepoAlbum @Inject constructor(
    appDatabase: AppDatabase,
    @Proxy private val lastFmService: LastFmService,
    private val albumGateway: AlbumGateway

) {

    private val dao = appDatabase.lastFmDao()

    suspend fun shouldFetch(albumId: Long): Boolean {
        return dao.getAlbum(albumId) == null
    }

    suspend fun get(albumId: Long): LastFmAlbum? {

        val cachedValue = getFromCache(albumId)
        if (cachedValue != null) {
            return cachedValue
        }

        val album = albumGateway.getByParam(albumId).awaitFirst() ?: return null
        if (album.hasSameNameAsFolder) {
            return null
        }
        return fetch(album)
    }

    private suspend fun getFromCache(albumId: Long): LastFmAlbum? {
        val album = dao.getAlbum(albumId)
        return album?.toDomain()
    }

    private suspend fun fetch(album: Album): LastFmAlbum {

        val albumId = album.id

        try {
            val albumInfo = lastFmService.getAlbumInfoAsync(album.title, album.artist).await().toDomain(albumId)
            return cache(albumInfo).toDomain()
        } catch (ex: Exception) {
            try {
                var searchedAlbum = lastFmService.searchAlbumAsync(album.title).await().toDomain(albumId, album.artist)
                yield()
                try {
                    searchedAlbum = lastFmService.getAlbumInfoAsync(searchedAlbum.title, searchedAlbum.artist).await()
                        .toDomain(albumId)
                } catch (ignored: Exception) {
                }
                return cache(searchedAlbum).toDomain()
            } catch (ex: Exception) {
                return cacheEmpty(albumId).toDomain()
            }

        }
    }

    private suspend fun cache(model: LastFmAlbum): LastFmAlbumEntity {
        val entity = model.toModel()
        dao.insertAlbum(entity)
        return entity
    }

    private suspend fun cacheEmpty(albumId: Long): LastFmAlbumEntity {
        val entity = LastFmNulls.createNullAlbum(albumId)
        dao.insertAlbum(entity)
        return entity
    }

    suspend fun delete(albumId: Long) {
        dao.deleteAlbum(albumId)
    }

}