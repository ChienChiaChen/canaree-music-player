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
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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
            return cacheAsync(albumInfo).await().toDomain()
        } catch (ex: Exception){
            try {
                var searchedAlbum = lastFmService.searchAlbumAsync(album.title).await().toDomain(albumId, album.artist)
                try {
                    searchedAlbum = lastFmService.getAlbumInfoAsync(searchedAlbum.title, searchedAlbum.artist).await().toDomain(albumId)
                } catch (ignored: Exception){}
                return cacheAsync(searchedAlbum).await().toDomain()
            } catch (ex: Exception){
                return cacheEmptyAsync(albumId).await().toDomain()
            }

        }
    }

    private suspend fun cacheAsync(model: LastFmAlbum): Deferred<LastFmAlbumEntity> = GlobalScope.async {
        assertBackgroundThread()
        val entity = model.toModel()
        dao.insertAlbum(entity)
        entity
    }

    private suspend fun cacheEmptyAsync(albumId: Long): Deferred<LastFmAlbumEntity> = GlobalScope.async {
        assertBackgroundThread()
        val entity = LastFmNulls.createNullAlbum(albumId)
        dao.insertAlbum(entity)
        entity
    }

    suspend fun delete(albumId: Long) = GlobalScope.launch {
        assertBackgroundThread()
        dao.deleteAlbum(albumId)
    }

}