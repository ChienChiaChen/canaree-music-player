package dev.olog.msc.data.repository

import android.content.Context
import android.provider.MediaStore.Audio.Media
import android.provider.MediaStore.Audio.Playlists
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.PrefsKeys
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.data.request.DataRequest
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.ItemRequest
import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.entity.track.Playlist
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.FavoriteGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.core.gateway.track.PlaylistGatewayHelper
import dev.olog.msc.core.gateway.track.SongGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.PlaylistMostPlayedEntity
import dev.olog.msc.data.entity.custom.*
import dev.olog.msc.data.mapper.toArtist
import dev.olog.msc.data.mapper.toPlaylist
import dev.olog.msc.data.mapper.toPlaylistSong
import dev.olog.msc.data.mapper.toSong
import dev.olog.msc.data.repository.queries.PlaylistQueries
import dev.olog.msc.data.repository.queries.TrackQueries
import dev.olog.msc.data.repository.util.ContentObserverFlow
import dev.olog.msc.data.repository.util.queryCountRow
import dev.olog.msc.data.repository.util.queryFirstColumn
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

internal class PlaylistRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val favoriteGateway: FavoriteGateway,
    appDatabase: AppDatabase,
    helper: PlaylistRepositoryHelper,
    private val prefsGateway: AppPreferencesGateway,
    prefsKeys: PrefsKeys,
    private val contentObserverFlow: ContentObserverFlow,
    private val songGateway: SongGateway

) : PlaylistGateway, PlaylistGatewayHelper by helper {

    private val contentResolver = context.contentResolver
    private val queries = PlaylistQueries(prefsGateway, contentResolver)
    private val trackQueries = TrackQueries(prefsGateway, false, contentResolver)

    private val resources = context.resources

    private val mostPlayedDao = appDatabase.playlistMostPlayedDao()
    private val historyDao = appDatabase.historyDao()

    private val autoPlaylistTitles = resources.getStringArray(prefsKeys.autoPlaylist())

    private fun createAutoPlaylist(id: Long, title: String): Playlist {
        return Playlist(id, title, 0)
    }

    override fun getAll(): DataRequest<Playlist> {
        return PageRequestImpl(
            cursorFactory = { queries.getAll(it) },
            cursorMapper = { it.toPlaylist() },
            listMapper = { playlistList ->
                playlistList.map { playlist ->
                    // get the size for every playlist
                    val sizeQueryCursor = queries.countPlaylistSize(playlist.id)
                    val sizeQuery = contentResolver.queryCountRow(sizeQueryCursor)
                    playlist.copy(size = sizeQuery)
                }
            },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = Playlists.EXTERNAL_CONTENT_URI
        )
    }

    override fun getByParam(param: Long): ItemRequest<Playlist> {
        if (PlaylistGateway.isAutoPlaylist(param)) {
            val item = getAllAutoPlaylists().first { it.id == param }
            return ItemRequestImmutable(item)
        }
        return ItemRequestImpl(
            cursorFactory = { queries.getById(param) },
            cursorMapper = { it.toPlaylist() },
            itemMapper = { playlist ->
                // get the size for every playlist
                val sizeQueryCursor = queries.countPlaylistSize(playlist.id)
                val sizeQuery = contentResolver.queryCountRow(sizeQueryCursor)
                playlist.copy(size = sizeQuery)
            },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun getAllAutoPlaylists(): List<Playlist> {
        return listOf(
            createAutoPlaylist(PlaylistGateway.LAST_ADDED_ID, autoPlaylistTitles[0]),
            createAutoPlaylist(PlaylistGateway.FAVORITE_LIST_ID, autoPlaylistTitles[1]),
            createAutoPlaylist(PlaylistGateway.HISTORY_LIST_ID, autoPlaylistTitles[2])
        )
    }

    override fun getSongListByParam(param: Long): DataRequest<Song> {
        if (param == PlaylistGateway.LAST_ADDED_ID) {
            return PageRequestImpl(
                cursorFactory = { trackQueries.getByLastAdded(it) },
                cursorMapper = { it.toSong() },
                listMapper = null,
                contentResolver = contentResolver,
                contentObserverFlow = contentObserverFlow,
                mediaStoreUri = Media.EXTERNAL_CONTENT_URI
            )
        }
        if (param == PlaylistGateway.FAVORITE_LIST_ID) {
            return PageRequestDao(
                cursorFactory = { request ->
                    // TODO sort by now is lost, repair
                    val page = request.page // TODO add filter
                    val favoritesIds = favoriteGateway.getAll(page.limit, page.offset)
                    trackQueries.getExisting(favoritesIds.joinToString { "'$it'" })
                },
                cursorMapper = { it.toSong() },
                listMapper = { list, request ->
                    val page = request.page // TODO add filter
                    val favoritesIds = favoriteGateway.getAll(page.limit, page.offset)
                    favoritesIds.asSequence()
                        .mapNotNull { fav -> list.first { it.id == fav } }
                        .toList()
                },
                contentResolver = contentResolver,
                changeNotification = { favoriteGateway.observeAll().asFlow() },
                overrideSize = favoriteGateway.countAll()
            )
        }
        if (param == PlaylistGateway.HISTORY_LIST_ID) {
            return PageRequestDao(
                cursorFactory = { request ->
                    val page = request.page // TODO add filter
                    val historyIds = historyDao.getAll(page.limit, page.offset)
                    trackQueries.getExisting(historyIds.joinToString { "'${it.songId}'" })
                },
                cursorMapper = { it.toSong() },
                listMapper = { list, request ->
                    val page = request.page // TODO add filter
                    val historyIds = historyDao.getAll(page.limit, page.offset)
                    historyIds.asSequence()
                        .mapNotNull { hist -> list.first { it.id == hist.songId } to hist }
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

        return PageRequestImpl(
            cursorFactory = { queries.getSongList(param, it) },
            cursorMapper = { it.toPlaylistSong() },
            listMapper = null,
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = Playlists.Members.getContentUri("external", param)
        )
    }

    override fun getSongListByParamDuration(param: Long, filter: Filter): Int {
        // TODO is needed for auto playlist?
        return when (param) {
            PlaylistGateway.LAST_ADDED_ID -> 0
            PlaylistGateway.FAVORITE_LIST_ID -> 0
            PlaylistGateway.HISTORY_LIST_ID -> 0
            else -> contentResolver.queryFirstColumn(queries.getSongListDuration(param, filter))
        }
    }

    override fun getSiblings(mediaId: MediaId): DataRequest<Playlist> {
        return PageRequestImpl(
            cursorFactory = { queries.getSiblings(mediaId.categoryId, it) },
            cursorMapper = { it.toPlaylist() },
            listMapper = { playlistList ->
                playlistList.map { playlist ->
                    // get the size for every playlist
                    val sizeQueryCursor = queries.countPlaylistSize(playlist.id)
                    val sizeQuery = contentResolver.queryCountRow(sizeQueryCursor)
                    playlist.copy(size = sizeQuery)
                }
            },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = Playlists.EXTERNAL_CONTENT_URI
        )
    }

    override fun canShowSiblings(mediaId: MediaId, filter: Filter): Boolean {
        return getSiblings(mediaId).getCount(filter) > 0
    }

    override fun getRelatedArtists(mediaId: MediaId): DataRequest<Artist> {
        if (PlaylistGateway.isAutoPlaylist(mediaId.categoryId)) {
            // auto playlist has not related artists
            return PageRequestStub()
        }
        return PageRequestImpl(
            cursorFactory = { queries.getRelatedArtists(mediaId.categoryId, it) },
            cursorMapper = { it.toArtist() },
            listMapper = null,
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun canShowRelatedArtists(mediaId: MediaId, filter: Filter): Boolean {
        if (PlaylistGateway.isAutoPlaylist(mediaId.categoryId)) {
            return false
        }
        return getRelatedArtists(mediaId).getCount(filter) > 0 &&
                prefsGateway.getVisibleTabs()[2]
    }


    private fun getMostPlayedSize(mediaId: MediaId): Int {
        if (PlaylistGateway.isAutoPlaylist(mediaId.categoryId)) {
            return 0
        }
        return mostPlayedDao.count(mediaId.categoryId)
    }

    override fun canShowMostPlayed(mediaId: MediaId): Boolean {
        return getMostPlayedSize(mediaId) > 0 && prefsGateway.getVisibleTabs()[0]
    }

    override fun getMostPlayed(mediaId: MediaId): DataRequest<Song> {
        if (PlaylistGateway.isAutoPlaylist(mediaId.categoryId)) {
            return PageRequestStub()
        }
        val maxAllowed = 10
        return PageRequestDao(
            cursorFactory = {
                val mostPlayed = mostPlayedDao.query(mediaId.categoryId, maxAllowed)
                queries.getExisting(mostPlayed.joinToString { "'${it.songId}'" })
            },
            cursorMapper = { it.toSong() },
            listMapper = { list, _ ->
                val mostPlayed = mostPlayedDao.query(mediaId.categoryId, maxAllowed)
                mostPlayed.asSequence()
                    .mapNotNull { mostPlayed -> list.firstOrNull { it.id == mostPlayed.songId } }
                    .take(maxAllowed)
                    .toList()
            },
            contentResolver = contentResolver,
            changeNotification = { mostPlayedDao.observe(mediaId.categoryId, maxAllowed).asFlow() }
        )
    }

    override suspend fun insertMostPlayed(mediaId: MediaId) {
        val songId = mediaId.leaf!!
        val playlistId = mediaId.categoryValue.toLong()
        songGateway.getByParam(songId).getItem()?.let { song ->
            mostPlayedDao.insertOne(PlaylistMostPlayedEntity(0, song.id, playlistId))
        }
    }
}