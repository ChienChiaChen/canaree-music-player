package dev.olog.msc.data.repository

import android.content.Context
import android.provider.MediaStore.Audio.Media
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.data.request.DataRequest
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.ItemRequest
import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.entity.track.Folder
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.prefs.SortPreferencesGateway
import dev.olog.msc.core.gateway.track.FolderGateway
import dev.olog.msc.core.gateway.track.SongGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.FolderMostPlayedEntity
import dev.olog.msc.data.entity.custom.ItemRequestImpl
import dev.olog.msc.data.entity.custom.PageRequestDao
import dev.olog.msc.data.entity.custom.PageRequestImpl
import dev.olog.msc.data.mapper.toArtist
import dev.olog.msc.data.mapper.toFolder
import dev.olog.msc.data.mapper.toSong
import dev.olog.msc.data.repository.queries.FolderQueries
import dev.olog.msc.data.repository.util.ContentObserverFlow
import dev.olog.msc.data.repository.util.queryFirstColumn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

internal class FolderRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val contentObserverFlow: ContentObserverFlow,
    appDatabase: AppDatabase,
    private val prefsGateway: AppPreferencesGateway,
    private val songGateway: SongGateway,
    sortGateway: SortPreferencesGateway

) : FolderGateway {

    private val contentResolver = context.contentResolver
    private val queries = FolderQueries(prefsGateway, sortGateway, contentResolver)

    private val mostPlayedDao = appDatabase.folderMostPlayedDao()

    override fun getAll(): DataRequest<Folder> {
        return PageRequestImpl(
            cursorFactory = { queries.getAll(it) },
            cursorMapper = { it.toFolder() },
            listMapper = null,
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun getByParam(param: String): ItemRequest<Folder> {
        return ItemRequestImpl(
            cursorFactory = { queries.getByPath(param) },
            cursorMapper = { it.toFolder() },
            itemMapper = null,
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun getSongListByParam(param: String): DataRequest<Song> {
        return PageRequestImpl(
            cursorFactory = { queries.getSongList(param, it) },
            cursorMapper = { it.toSong() },
            listMapper = null,
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun getSongListByParamDuration(param: String, filter: Filter): Int {
        return contentResolver.queryFirstColumn(queries.getSongListDuration(param, filter))
    }

    override fun getRecentlyAddedSongs(mediaId: MediaId): DataRequest<Song> {
        return PageRequestImpl(
            cursorFactory = { queries.getRecentlyAddedSongs(mediaId.categoryValue, it) },
            cursorMapper = { it.toSong() },
            listMapper = null,
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun canShowRecentlyAddedSongs(mediaId: MediaId): Boolean {
        return getRecentlyAddedSongs(mediaId).getCount(Filter.NO_FILTER) > 0 && prefsGateway.getVisibleTabs()[1]
    }

    override fun getSiblings(mediaId: MediaId): DataRequest<Folder> {
        return PageRequestImpl(
            cursorFactory = { queries.getSiblings(mediaId.categoryValue, it) },
            cursorMapper = { it.toFolder() },
            listMapper = null,
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun canShowSiblings(mediaId: MediaId, filter: Filter): Boolean {
        return getSiblings(mediaId).getCount(filter) > 0
    }

    override fun getRelatedArtists(mediaId: MediaId): DataRequest<Artist> {
        return PageRequestImpl(
            cursorFactory = { queries.getRelatedArtists(mediaId.categoryValue, it) },
            cursorMapper = { it.toArtist() },
            listMapper = null,
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun canShowRelatedArtists(mediaId: MediaId, filter: Filter): Boolean {
        return getRelatedArtists(mediaId).getCount(filter) > 0 &&
                prefsGateway.getVisibleTabs()[2]
    }

    private fun getMostPlayedSize(mediaId: MediaId): Int {
        return mostPlayedDao.count(mediaId.categoryValue)
    }

    override fun canShowMostPlayed(mediaId: MediaId): Boolean {
        return getMostPlayedSize(mediaId) > 0 && prefsGateway.getVisibleTabs()[0]
    }

    override fun getMostPlayed(mediaId: MediaId): DataRequest<Song> {
        val maxAllowed = 10
        return PageRequestDao(
            cursorFactory = {
                val mostPlayed = mostPlayedDao.query(mediaId.categoryValue, maxAllowed)
                queries.getExisting(mediaId.categoryValue, mostPlayed.joinToString { "'${it.songId}'" })
            },
            cursorMapper = { it.toSong() },
            listMapper = { list, _ ->
                val mostPlayed = mostPlayedDao.query(mediaId.categoryValue, maxAllowed)
                mostPlayed.asSequence()
                    .mapNotNull { mostPlayed -> list.firstOrNull { it.id == mostPlayed.songId } }
                    .take(maxAllowed)
                    .toList()
            },
            contentResolver = contentResolver,
            changeNotification = { mostPlayedDao.observe(mediaId.categoryValue, maxAllowed).asFlow() }
        )
    }

    override suspend fun insertMostPlayed(mediaId: MediaId) {
        val songId = mediaId.leaf!!
        songGateway.getByParam(songId).getItem()?.let { song ->
            mostPlayedDao.insertOne(FolderMostPlayedEntity(0, song.id, song.folderPath))
        }

    }

    override fun getAllUnfiltered(): Flow<List<Folder>> {
        TODO()
//        return songGateway.getAllUnfiltered()
//            .map(this::mapToFolderList)
    }

}