package dev.olog.msc.data.repository.podcast

import android.content.Context
import android.provider.MediaStore
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.data.request.DataRequest
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.ItemRequest
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.entity.podcast.PodcastArtist
import dev.olog.msc.core.gateway.podcast.PodcastArtistGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.prefs.SortPreferencesGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.custom.ItemRequestImpl
import dev.olog.msc.data.entity.custom.PageRequestDao
import dev.olog.msc.data.entity.custom.PageRequestImpl
import dev.olog.msc.data.mapper.toPodcast
import dev.olog.msc.data.mapper.toPodcastAlbum
import dev.olog.msc.data.mapper.toPodcastArtist
import dev.olog.msc.data.repository.queries.ArtistQueries
import dev.olog.msc.data.repository.util.ContentObserverFlow
import dev.olog.msc.data.repository.util.queryCountRow
import dev.olog.msc.data.repository.util.queryFirstColumn
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

internal class PodcastArtistRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    appDatabase: AppDatabase,
    private val prefsGateway: AppPreferencesGateway,
    private val contentObserverFlow: ContentObserverFlow,
    sortGateway: SortPreferencesGateway

) : PodcastArtistGateway {

    private val contentResolver = context.contentResolver
    private val queries = ArtistQueries(prefsGateway, sortGateway, true, contentResolver)

    private val lastPlayedDao = appDatabase.lastPlayedPodcastArtistDao()

    override fun getAll(): DataRequest<PodcastArtist> {
        return PageRequestImpl(
            cursorFactory = { queries.getAll(it) },
            cursorMapper = { it.toPodcastArtist() },
            listMapper = null,
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun getByParam(param: Long): ItemRequest<PodcastArtist> {
        return ItemRequestImpl(
            cursorFactory = { queries.getById(param) },
            cursorMapper = { it.toPodcastArtist() },
            itemMapper = null,
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun getLastPlayed(): DataRequest<PodcastArtist> {
        val maxAllowed = 10
        return PageRequestDao(
            cursorFactory = {
                val lastPlayed = lastPlayedDao.getAll(maxAllowed)
                queries.getExistingLastPlayed(lastPlayed.joinToString { "'${it.id}'" })
            },
            cursorMapper = { it.toPodcastArtist() },
            listMapper = { list, _ ->
                val lastPlayed = lastPlayedDao.getAll(maxAllowed)
                lastPlayed.asSequence()
                    .mapNotNull { last -> list.firstOrNull { it.id == last.id } }
                    .take(maxAllowed)
                    .toList()
            },
            contentResolver = contentResolver,
            changeNotification = { lastPlayedDao.observeAll(1).asFlow() }
        )
    }

    override fun canShowLastPlayed(): Boolean {
        return prefsGateway.canShowLibraryRecentPlayedVisibility() &&
                getAll().getCount(Filter.NO_FILTER) >= 5 &&
                lastPlayedDao.getCount() > 0
    }

    override fun getRecentlyAdded(): DataRequest<PodcastArtist> {
        return PageRequestImpl(
            cursorFactory = { queries.getRecentlyAdded(it) },
            cursorMapper = { it.toPodcastArtist() },
            listMapper = null,
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun getPodcastListByParam(param: Long): DataRequest<Podcast> {
        return PageRequestImpl(
            cursorFactory = { queries.getSongList(param, it) },
            cursorMapper = { it.toPodcast() },
            listMapper = null,
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun getPodcastListByParamDuration(param: Long, filter: Filter): Int {
        return contentResolver.queryFirstColumn(queries.getSongListDuration(param, filter))
    }

    override fun getSiblings(mediaId: MediaId): DataRequest<PodcastAlbum> {
        return PageRequestImpl(
            cursorFactory = { queries.getSiblings(mediaId.categoryId, it) },
            cursorMapper = { it.toPodcastAlbum() },
            listMapper = null,
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun canShowSiblings(mediaId: MediaId, filter: Filter): Boolean {
        return getSiblings(mediaId).getCount(filter) > 0
    }

    override fun canShowRecentlyAdded(filter: Filter): Boolean {
        val cursor = queries.getRecentlyAdded(null)
        return prefsGateway.canShowLibraryNewVisibility() &&
                contentResolver.queryCountRow(cursor) > 0
    }

    override suspend fun addLastPlayed(id: Long) {
        lastPlayedDao.insertOne(id)
    }

}