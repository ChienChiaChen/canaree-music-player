package dev.olog.msc.data.repository.podcast

import android.content.Context
import android.content.res.Resources
import android.provider.MediaStore
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.PrefsKeys
import dev.olog.msc.core.coroutines.mapToList
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.ItemRequest
import dev.olog.msc.core.entity.Page
import dev.olog.msc.core.entity.PageRequest
import dev.olog.msc.core.entity.favorite.FavoriteType
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.podcast.PodcastPlaylist
import dev.olog.msc.core.gateway.FavoriteGateway
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.PodcastPlaylistEntity
import dev.olog.msc.data.entity.PodcastPlaylistTrackEntity
import dev.olog.msc.data.entity.custom.ItemRequestDao
import dev.olog.msc.data.entity.custom.ItemRequestImmutable
import dev.olog.msc.data.entity.custom.PageRequestDao
import dev.olog.msc.data.entity.custom.PageRequestImpl
import dev.olog.msc.data.mapper.toPodcast
import dev.olog.msc.data.repository.queries.TrackQueries
import dev.olog.msc.data.repository.util.ContentObserverFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

internal class PodcastPlaylistRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    resources: Resources,
    appDatabase: AppDatabase,
    private val favoriteGateway: FavoriteGateway,
    prefsKeys: PrefsKeys,
    prefsGateway: AppPreferencesGateway,
    private val contentObserverFlow: ContentObserverFlow

) : PodcastPlaylistGateway {

    private val podcastPlaylistDao = appDatabase.podcastPlaylistDao()
    private val historyDao = appDatabase.historyDao()

    private val contentResolver = context.contentResolver
    private val trackQueries = TrackQueries(prefsGateway, true, contentResolver)

    private fun PodcastPlaylistEntity.toDomain(): PodcastPlaylist {
        return PodcastPlaylist(
            this.id,
            this.name,
            this.size,
            ""
        )
    }

    private val autoPlaylistTitles = resources.getStringArray(prefsKeys.autoPlaylist())

    private fun createAutoPlaylist(id: Long, title: String): PodcastPlaylist {
        return PodcastPlaylist(id, title, 0, "")
    }

    override fun getAllAutoPlaylists(): List<PodcastPlaylist> {
        return listOf(
            createAutoPlaylist(PodcastPlaylistGateway.PODCAST_LAST_ADDED_ID, autoPlaylistTitles[0]),
            createAutoPlaylist(PodcastPlaylistGateway.PODCAST_FAVORITE_LIST_ID, autoPlaylistTitles[1]),
            createAutoPlaylist(PodcastPlaylistGateway.PODCAST_HISTORY_LIST_ID, autoPlaylistTitles[2])
        )
    }

    override fun getAll(): PageRequest<PodcastPlaylist> {
        return object : PageRequest<PodcastPlaylist> {
            override fun getPage(page: Page): List<PodcastPlaylist> {
                return podcastPlaylistDao.getChunk(page.limit, page.offset).map { it.toDomain() }
            }

            override fun getCount(): Int {
                return podcastPlaylistDao.getCount()
            }

            override suspend fun observePage(page: Page): Flow<List<PodcastPlaylist>> {
                return podcastPlaylistDao.observeAll()
                    .asFlow()
                    .distinctUntilChanged()
                    .mapToList { it.toDomain() }

            }

            override suspend fun observeNotification(): Flow<Unit> {
                return podcastPlaylistDao.observeAll()
                    .distinctUntilChanged()
                    .asFlow()
                    .drop(1)
                    .map { Unit }

            }
        }
    }

    override fun getByParam(param: Long): ItemRequest<PodcastPlaylist> {
        if (PodcastPlaylistGateway.isPodcastAutoPlaylist(param)) {
            val item = getAllAutoPlaylists().first { it.id == param }
            return ItemRequestImmutable(item)
        }

        return ItemRequestDao(
            getItemByParam = { podcastPlaylistDao.getPlaylist(param).toDomain() },
            observeItemByParam = { podcastPlaylistDao.observeById(param).asFlow().map { it.toDomain() } },
            param = param
        )
    }

    override fun getPodcastListByParamDuration(param: Long): Int {
        return 0
    }

    override fun getPodcastListByParam(param: Long): PageRequest<Podcast> {
        if (param == PodcastPlaylistGateway.PODCAST_LAST_ADDED_ID) {
            return PageRequestImpl(
                cursorFactory = { trackQueries.getByLastAdded(it) },
                cursorMapper = { it.toPodcast() },
                listMapper = {
                    PodcastRepository.adjustImages(context, it)
                },
                contentResolver = contentResolver,
                contentObserverFlow = contentObserverFlow,
                mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            )
        }
        if (param == PodcastPlaylistGateway.PODCAST_FAVORITE_LIST_ID) {
            return PageRequestDao(
                cursorFactory = { page ->
                    // TODO sort by now is lost, repair
                    val favoritesIds = favoriteGateway.getAllPodcasts(page?.limit ?: Int.MAX_VALUE, page?.offset ?: 0)
                    trackQueries.getExisting(favoritesIds.joinToString { "'$it'" })
                },
                cursorMapper = { it.toPodcast() },
                listMapper = { list, page ->
                    val existing = PodcastRepository.adjustImages(context, list)
                    val favoritesIds = favoriteGateway.getAll(page?.limit ?: Int.MAX_VALUE, page?.offset ?: 0)
                    favoritesIds.asSequence()
                        .mapNotNull { fav -> existing.first { it.id == fav } }
                        .toList()
                },
                contentResolver = contentResolver,
                changeNotification = { favoriteGateway.observeAll().asFlow() },
                overrideSize = favoriteGateway.countAll()
            )
        }
        if (param == PodcastPlaylistGateway.PODCAST_HISTORY_LIST_ID) {
            return PageRequestDao(
                cursorFactory = { page ->
                    val historyIds = historyDao.getAllPodcasts(page?.limit ?: Int.MAX_VALUE, page?.offset ?: 0)
                    trackQueries.getExisting(historyIds.joinToString { "'${it.podcastId}'" })
                },
                cursorMapper = { it.toPodcast() },
                listMapper = { list, page ->
                    val existing = PodcastRepository.adjustImages(context, list)
                    val historyIds = historyDao.getAll(page?.limit ?: Int.MAX_VALUE, page?.offset ?: 0)
                    historyIds.asSequence()
                        .mapNotNull { hist -> existing.first { it.id == hist.songId } to hist }
                        .map {
                            it.first.copy(
                                trackNumber = it.second.id,
                                dateAdded = it.second.dateAdded
                            )
                        } // TODO check behavior
                        .sortedByDescending { it.dateAdded }
                        .toList()
                },
                contentResolver = contentResolver,
                changeNotification = { favoriteGateway.observeAll().asFlow() },
                overrideSize = historyDao.countAll()
            )
        }
        return PageRequestDao(
            cursorFactory = { page ->
                val podcastIds = podcastPlaylistDao.getPlaylistTracks(param, page.offset, page.limit)
                trackQueries.getExisting(podcastIds.joinToString { "'$it'" })
            },
            cursorMapper = { it.toPodcast() },
            listMapper = { list, page ->
                val existing = PodcastRepository.adjustImages(context, list)
                podcastPlaylistDao.getPlaylistTracks(param, page.offset, page.limit)
                    .asSequence()
                    .mapNotNull { podcast ->
                        existing.first { it.id == podcast.podcastId }
                            .copy(trackNumber = podcast.idInPlaylist.toInt())
                    }
                    .toList()
            },
            contentResolver = contentResolver,
            changeNotification = { podcastPlaylistDao.observePlaylistTracks(param, 0, Int.MAX_VALUE).asFlow() },
            overrideSize = podcastPlaylistDao.countPlaylistTracks(param)
        )
    }

    override fun getSiblings(mediaId: MediaId): PageRequest<PodcastPlaylist> {
        return object : PageRequest<PodcastPlaylist> {
            override fun getPage(page: Page): List<PodcastPlaylist> {
                return podcastPlaylistDao.getSiblings(mediaId.categoryId, page.limit, page.offset)
                    .map { it.toDomain() }
            }

            override fun getCount(): Int {
                return podcastPlaylistDao.countSiblings(mediaId.categoryId)
            }

            override suspend fun observePage(page: Page): Flow<List<PodcastPlaylist>> {
                return podcastPlaylistDao.observeSibling(mediaId.categoryId)
                    .asFlow()
                    .distinctUntilChanged()
                    .mapToList { it.toDomain() }
            }

            override suspend fun observeNotification(): Flow<Unit> {
                return podcastPlaylistDao.observeSibling(mediaId.categoryId)
                    .asFlow()
                    .drop(1)
                    .map { Unit }
            }
        }
    }

    override fun canShowSiblings(mediaId: MediaId): Boolean {
        return !PodcastPlaylistGateway.isPodcastAutoPlaylist(mediaId.categoryId) &&
                podcastPlaylistDao.countSiblings(mediaId.categoryId) > 0
    }

    override fun getPlaylistsBlocking(): List<PodcastPlaylist> {
        return podcastPlaylistDao.getAllPlaylistsBlocking().map { it.toDomain() }
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        podcastPlaylistDao.deletePlaylist(playlistId)
    }

    override suspend fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>) {
        var maxIdInPlaylist = podcastPlaylistDao.getPlaylistMaxId(playlistId).toLong()
        val tracks = songIds.map {
            PodcastPlaylistTrackEntity(
                playlistId = playlistId, idInPlaylist = ++maxIdInPlaylist,
                podcastId = it
            )
        }
        podcastPlaylistDao.insertTracks(tracks)
    }

    override suspend fun createPlaylist(playlistName: String): Long {
        return podcastPlaylistDao.createPlaylist(PodcastPlaylistEntity(name = playlistName, size = 0))
    }

    override suspend fun clearPlaylist(playlistId: Long) {
        when (playlistId) {
            PodcastPlaylistGateway.PODCAST_FAVORITE_LIST_ID -> {
                favoriteGateway.deleteAll(FavoriteType.PODCAST)
            }
            PodcastPlaylistGateway.PODCAST_HISTORY_LIST_ID -> {
                historyDao.deleteAllPodcasts()
            }
            else -> {
                podcastPlaylistDao.clearPlaylist(playlistId)
            }
        }
    }

    override suspend fun removeSongFromPlaylist(playlistId: Long, idInPlaylist: Long) {
        if (PodcastPlaylistGateway.isPodcastAutoPlaylist(playlistId)) {
            return removeFromAutoPlaylist(playlistId, idInPlaylist)
        } else {
            podcastPlaylistDao.deleteTrack(playlistId, idInPlaylist)
        }

    }

    private suspend fun removeFromAutoPlaylist(playlistId: Long, songId: Long) {
        when (playlistId) {
            PodcastPlaylistGateway.PODCAST_FAVORITE_LIST_ID -> favoriteGateway.deleteSingle(
                FavoriteType.PODCAST,
                songId
            )
            PodcastPlaylistGateway.PODCAST_HISTORY_LIST_ID -> historyDao.deleteSinglePodcast(songId)
            else -> throw IllegalArgumentException("invalid auto playlist id: $playlistId")
        }
    }

    override suspend fun renamePlaylist(playlistId: Long, newTitle: String) {
        podcastPlaylistDao.renamePlaylist(playlistId, newTitle)
    }

    override suspend fun removeDuplicated(playlistId: Long) {
        podcastPlaylistDao.removeDuplicated(playlistId)
    }

    override suspend fun insertPodcastToHistory(podcastId: Long) {
        return historyDao.insertPodcasts(podcastId)
    }
}