package dev.olog.msc.data.repository

import android.content.Context
import android.provider.MediaStore.Audio.Media
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.ItemRequest
import dev.olog.msc.core.entity.PageRequest
import dev.olog.msc.core.entity.track.Album
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.track.AlbumGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.custom.ItemRequestImpl
import dev.olog.msc.data.entity.custom.PageRequestDao
import dev.olog.msc.data.entity.custom.PageRequestImpl
import dev.olog.msc.data.mapper.toAlbum
import dev.olog.msc.data.mapper.toSong
import dev.olog.msc.data.repository.queries.AlbumQueries
import dev.olog.msc.data.repository.util.ContentObserverFlow
import dev.olog.msc.data.repository.util.queryCountRow
import dev.olog.msc.data.repository.util.querySize
import dev.olog.msc.imageprovider.ImagesFolderUtils
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

internal class AlbumRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    appDatabase: AppDatabase,
    private val usedImageGateway: UsedImageGateway,
    private val contentObserverFlow: ContentObserverFlow,
    private val prefsGateway: AppPreferencesGateway

) : AlbumGateway {

    companion object {
        internal fun updateImages(
            context: Context,
            list: List<Album>,
            usedImageGateway: UsedImageGateway
        ): List<Album> {
            val allForAlbum = usedImageGateway.getAllForAlbums()
            if (allForAlbum.isEmpty()) {
                return list
            }

            return list.map { album ->
                val image =
                    allForAlbum.firstOrNull { it.id == album.id }?.image ?: ImagesFolderUtils.forAlbum(
                        context,
                        album.id
                    )
                album.copy(image = image)
            }
        }
    }

    private val contentResolver = context.contentResolver
    private val queries = AlbumQueries(prefsGateway, false, contentResolver)

    private val lastPlayedDao = appDatabase.lastPlayedAlbumDao()

    override fun getAll(): PageRequest<Album> {
        return PageRequestImpl(
            cursorFactory = { queries.getAll(it) },
            cursorMapper = { it.toAlbum() },
            listMapper = { updateImages(context, it, usedImageGateway) },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun getByParam(param: Long): ItemRequest<Album> {
        return ItemRequestImpl(
            cursorFactory = { queries.getById(param) },
            cursorMapper = { it.toAlbum() },
            itemMapper = { updateImages(context, listOf(it), usedImageGateway).first() },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun getLastPlayed(): PageRequest<Album> {
        val maxAllowed = 10
        return PageRequestDao(
            cursorFactory = {
                val lastPlayed = lastPlayedDao.getAll(maxAllowed)
                queries.getExistingLastPlayed(lastPlayed.joinToString { "'${it.id}'" })
            },
            cursorMapper = { it.toAlbum() },
            listMapper = { list, _ ->
                val lastPlayed = lastPlayedDao.getAll(maxAllowed)
                val existing = updateImages(context, list, usedImageGateway)
                lastPlayed.asSequence()
                    .mapNotNull { last -> existing.firstOrNull { it.id == last.id } }
                    .take(maxAllowed)
                    .toList()
            },
            contentResolver = contentResolver,
            changeNotification = { lastPlayedDao.observeAll(1).asFlow() }
        )
    }

    override fun canShowLastPlayed(): Boolean {
        return prefsGateway.canShowLibraryRecentPlayedVisibility() &&
                getAll().getCount() >= 5 &&
                lastPlayedDao.getCount() > 0
    }

    override fun getRecentlyAdded(): PageRequest<Album> {
        return PageRequestImpl(
            cursorFactory = { queries.getRecentlyAdded(it) },
            cursorMapper = { it.toAlbum() },
            listMapper = { updateImages(context, it, usedImageGateway) },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun getSongListByParam(param: Long): PageRequest<Song> {
        return PageRequestImpl(
            cursorFactory = { queries.getSongList(param, it) },
            cursorMapper = { it.toSong() },
            listMapper = {
                val result = SongRepository.adjustImages(context, it)
                SongRepository.updateImages(result, usedImageGateway)
            },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun getSongListByParamDuration(param: Long): Int {
        return contentResolver.querySize(queries.getSongListDuration(param))
    }

    override fun getSiblings(mediaId: MediaId): PageRequest<Album> {
        return PageRequestImpl(
            cursorFactory = { queries.getSiblings(mediaId.categoryId, it) },
            cursorMapper = { it.toAlbum() },
            listMapper = { updateImages(context, it, usedImageGateway) },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun canShowSiblings(mediaId: MediaId): Boolean {
        return getSiblings(mediaId).getCount() > 0
    }

    override fun canShowRecentlyAdded(): Boolean {
        val cursor = queries.getRecentlyAdded(null)
        val size = contentResolver.queryCountRow(cursor)
        return prefsGateway.canShowLibraryNewVisibility() && size > 0
    }

    override suspend fun addLastPlayed(id: Long) {
        return lastPlayedDao.insertOne(id)
    }
}