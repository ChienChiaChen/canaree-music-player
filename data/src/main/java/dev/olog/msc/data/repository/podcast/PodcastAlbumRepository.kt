package dev.olog.msc.data.repository.podcast

import android.content.Context
import android.provider.MediaStore
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.ItemRequest
import dev.olog.msc.core.entity.PageRequest
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.custom.ItemRequestImpl
import dev.olog.msc.data.entity.custom.PageRequestDao
import dev.olog.msc.data.entity.custom.PageRequestImpl
import dev.olog.msc.data.mapper.toPodcast
import dev.olog.msc.data.mapper.toPodcastAlbum
import dev.olog.msc.data.repository.queries.AlbumQueries
import dev.olog.msc.data.repository.util.ContentObserverFlow
import dev.olog.msc.data.repository.util.queryCountRow
import dev.olog.msc.data.repository.util.querySize
import dev.olog.msc.imageprovider.ImagesFolderUtils
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

internal class PodcastAlbumRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    appDatabase: AppDatabase,
    private val usedImageGateway: UsedImageGateway,
    private val prefsGateway: AppPreferencesGateway,
    private val contentObserverFlow: ContentObserverFlow

) : PodcastAlbumGateway {

    companion object {
        internal fun updateImages(
            context: Context,
            list: List<PodcastAlbum>,
            usedImageGateway: UsedImageGateway
        ): List<PodcastAlbum> {
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
    private val queries = AlbumQueries(prefsGateway, true, contentResolver)

    private val lastPlayedDao = appDatabase.lastPlayedPodcastAlbumDao()

    override fun getAll(): PageRequest<PodcastAlbum> {
        return PageRequestImpl(
            cursorFactory = { queries.getAll(it) },
            cursorMapper = { it.toPodcastAlbum() },
            listMapper = { updateImages(context, it, usedImageGateway) },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun getByParam(param: Long): ItemRequest<PodcastAlbum> {
        return ItemRequestImpl(
            cursorFactory = { queries.getById(param) },
            cursorMapper = { it.toPodcastAlbum() },
            itemMapper = { updateImages(context, listOf(it), usedImageGateway).first() },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun getLastPlayed(): PageRequest<PodcastAlbum> {
        val maxAllowed = 10
        return PageRequestDao(
            cursorFactory = {
                val lastPlayed = lastPlayedDao.getAll(maxAllowed)
                queries.getExistingLastPlayed(lastPlayed.joinToString { "'${it.id}'" })
            },
            cursorMapper = { it.toPodcastAlbum() },
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

    override fun getRecentlyAdded(): PageRequest<PodcastAlbum> {
        return PageRequestImpl(
            cursorFactory = { queries.getRecentlyAdded(it) },
            cursorMapper = { it.toPodcastAlbum() },
            listMapper = { updateImages(context, it, usedImageGateway) },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun getPodcastListByParam(param: Long): PageRequest<Podcast> {
        return PageRequestImpl(
            cursorFactory = { queries.getSongList(param, it) },
            cursorMapper = { it.toPodcast() },
            listMapper = {
                PodcastRepository.adjustImages(context, it)
            },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun getPodcastListByParamDuration(param: Long): Int {
        return contentResolver.querySize(queries.getSongListDuration(param))
    }

    override fun getSiblings(mediaId: MediaId): PageRequest<PodcastAlbum> {
        return PageRequestImpl(
            cursorFactory = { queries.getSiblings(mediaId.categoryId, it) },
            cursorMapper = { it.toPodcastAlbum() },
            listMapper = { updateImages(context, it, usedImageGateway) },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
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
        lastPlayedDao.insertOne(id)
    }
}