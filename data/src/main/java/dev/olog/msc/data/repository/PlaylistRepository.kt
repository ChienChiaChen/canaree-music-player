package dev.olog.msc.data.repository

import android.content.Context
import android.provider.MediaStore
import androidx.core.math.MathUtils.clamp
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.PrefsKeys
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.ChunkRequest
import dev.olog.msc.core.entity.ChunkedData
import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.entity.track.Playlist
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.FavoriteGateway
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.core.gateway.track.PlaylistGatewayHelper
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.mapper.toArtist
import dev.olog.msc.data.mapper.toPlaylist
import dev.olog.msc.data.mapper.toSong
import dev.olog.msc.data.repository.queries.PlaylistQueries
import dev.olog.msc.data.repository.queries.TrackQueries
import dev.olog.msc.data.repository.util.*
import io.reactivex.Completable
import io.reactivex.Observable
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

internal class PlaylistRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val favoriteGateway: FavoriteGateway,
    appDatabase: AppDatabase,
    helper: PlaylistRepositoryHelper,
    private val prefsGateway: AppPreferencesGateway,
    prefsKeys: PrefsKeys,
    private val contentObserver: ContentObserver,
    private val usedImageGateway: UsedImageGateway,
    private val contentResolverFlow: ContentResolverFlow

) : PlaylistGateway, PlaylistGatewayHelper by helper {

    private val contentResolver = context.contentResolver
    private val playlistQueries = PlaylistQueries(prefsGateway, contentResolver)
    private val trackQueries = TrackQueries(prefsGateway, false, contentResolver)

    private val resources = context.resources

    private val mostPlayedDao = appDatabase.playlistMostPlayedDao()
    private val historyDao = appDatabase.historyDao()

    private val autoPlaylistTitles = resources.getStringArray(prefsKeys.autoPlaylist())

    override suspend fun getAll(): Flow<List<Playlist>> = flowOf()

    override fun observeSongListByParam(param: Long): Observable<List<Song>> {
        return Observable.just(listOf())
    }

    private fun createAutoPlaylist(id: Long, title: String): Playlist {
        return Playlist(id, title, 0, "")
    }

    override fun getChunk(): ChunkedData<Playlist> {
        return ChunkedData(
            chunkOf = { chunkRequest -> makeChunkOf(chunkRequest) },
            allDataSize = contentResolver.querySize(playlistQueries.countAll()),
            observeChanges = { contentObserver.createQuery(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI) }
        )
    }

    private fun makeChunkOf(chunkRequest: ChunkRequest): List<Playlist> {
        return contentResolver.queryAll(
            cursor = playlistQueries.getAll(chunkRequest),
            mapper = { it.toPlaylist(context, 0) },
            afterQuery = { playlistList ->
                playlistList.map { playlist ->
                    // get the size for every playlist
                    val sizeQueryCursor = playlistQueries.countPlaylistSize(playlist.id)
                    val sizeQuery = contentResolver.queryCountRow(sizeQueryCursor)
                    playlist.copy(size = sizeQuery)
                }
            })
    }

    override fun getByParam(param: Long): Playlist {
        if (PlaylistGateway.isAutoPlaylist(param)) {
            return getAllAutoPlaylists().first { it.id == param }
        }
        val cursor = playlistQueries.getById(param)
        return contentResolver.queryAll(cursor, { it.toPlaylist(context, 0) }, { playlistList ->
            playlistList.map { playlist ->
                // get the size for every playlist
                val sizeQueryCursor = playlistQueries.countPlaylistSize(playlist.id)
                val sizeQuery = contentResolver.queryCountRow(sizeQueryCursor)
                playlist.copy(size = sizeQuery)
            }
        }).first()
    }

    override suspend fun observeByParam(param: Long): Flow<Playlist> {
        return contentResolverFlow.createQuery<Playlist>({ playlistQueries.getById(param) }, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            .mapToOne { it.toPlaylist(context, 0) }
            .map { playlist ->
                val sizeQueryCursor = playlistQueries.countPlaylistSize(playlist.id)
                val sizeQuery = contentResolver.queryCountRow(sizeQueryCursor)
                playlist.copy(size = sizeQuery)
            }
            .distinctUntilChanged()
    }

    override fun getAllAutoPlaylists(): List<Playlist> {
        return listOf(
            createAutoPlaylist(PlaylistGateway.LAST_ADDED_ID, autoPlaylistTitles[0]),
            createAutoPlaylist(PlaylistGateway.FAVORITE_LIST_ID, autoPlaylistTitles[1]),
            createAutoPlaylist(PlaylistGateway.HISTORY_LIST_ID, autoPlaylistTitles[2])
        )
    }

    override fun getSongListByParamChunk(param: Long): ChunkedData<Song> {
        return ChunkedData(
            chunkOf = { chunkRequest -> makeSongListByParamChunk(param, chunkRequest) },
            allDataSize = getSongListCountByParam(param),
            observeChanges = observSongListByParam(param)
        )
    }

    override fun getSongListByParamDuration(param: Long): Int {
        // TODO is needed for auto playlist?
        return when (param) {
            PlaylistGateway.LAST_ADDED_ID -> 0
            PlaylistGateway.FAVORITE_LIST_ID -> 0
            PlaylistGateway.HISTORY_LIST_ID -> 0
            else -> contentResolver.querySize(playlistQueries.getSongListDuration(param))
        }
    }

    private fun getSongListCountByParam(playlistId: Long): Int {
        return when (playlistId) {
            PlaylistGateway.LAST_ADDED_ID -> {
                val cursor = trackQueries.countAll()
                contentResolver.querySize(cursor)
            }
            PlaylistGateway.FAVORITE_LIST_ID -> favoriteGateway.countAll()
            PlaylistGateway.HISTORY_LIST_ID -> historyDao.countAll()
            else -> {
                val cursor = playlistQueries.getSongList(playlistId, null)
                contentResolver.queryCountRow(cursor)
            }
        }
    }

    private fun makeSongListByParamChunk(playlistId: Long, chunkRequest: ChunkRequest): List<Song> {
        return when (playlistId) {
            PlaylistGateway.LAST_ADDED_ID -> {
                contentResolver.queryAll(trackQueries.getByLastAdded(chunkRequest), { it.toSong() },
                    {
                        val result = SongRepository.adjustImages(context, it)
                        SongRepository.updateImages(result, usedImageGateway)
                    })
            }
            PlaylistGateway.FAVORITE_LIST_ID -> {
                // TODO sort by now is lost, repair
                val favoritesIds = favoriteGateway.getAll(chunkRequest.limit, chunkRequest.offset)
                val existing = contentResolver.queryAll(trackQueries.getExisting(favoritesIds.joinToString { "'$it'" }),
                    { it.toSong() },
                    {
                        val result = SongRepository.adjustImages(context, it)
                        SongRepository.updateImages(result, usedImageGateway)
                    })
                favoritesIds.asSequence()
                    .mapNotNull { fav -> existing.first { it.id == fav } }
                    .toList()
            }
            PlaylistGateway.HISTORY_LIST_ID -> {
                val historyIds = historyDao.getAll(chunkRequest.limit, chunkRequest.offset)
                val existing =
                    contentResolver.queryAll(trackQueries.getExisting(historyIds.joinToString { "'${it.songId}'" }),
                        { it.toSong() },
                        {
                            val result = SongRepository.adjustImages(context, it)
                            SongRepository.updateImages(result, usedImageGateway)
                        })
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
            }
            else -> {
                val cursor = playlistQueries.getSongList(playlistId, chunkRequest)
                contentResolver.queryAll(cursor, { it.toSong() }, {
                    val result = SongRepository.adjustImages(context, it)
                    SongRepository.updateImages(result, usedImageGateway)
                })
            }
        }
    }

    private fun observSongListByParam(playlistId: Long): suspend () -> Flow<Unit> {
        return suspend {
            when (playlistId) {
                PlaylistGateway.LAST_ADDED_ID -> contentObserver.createQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
                PlaylistGateway.FAVORITE_LIST_ID -> favoriteGateway.observeAll().asFlow()
                    .drop(1)
                    .map { Unit }
                PlaylistGateway.HISTORY_LIST_ID -> historyDao.observeAll().asFlow()
                    .drop(1)
                    .map { Unit }
                else -> contentObserver.createQuery(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI)
            }
        }
    }

    private fun getSiblingsSize(mediaId: MediaId): Int {
        val cursor = playlistQueries.getSiblingsChunk(mediaId.categoryId, null)
        return contentResolver.queryCountRow(cursor)
    }

    override fun getSiblingsChunk(mediaId: MediaId): ChunkedData<Playlist> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = playlistQueries.getSiblingsChunk(mediaId.categoryId, chunkRequest)
                contentResolver.queryAll(cursor, { it.toPlaylist(context, 0) }, { playlistList ->
                    playlistList.map { playlist ->
                        // get the size for every playlist
                        val sizeQueryCursor = playlistQueries.countPlaylistSize(playlist.id)
                        val sizeQuery = contentResolver.queryCountRow(sizeQueryCursor)
                        playlist.copy(size = sizeQuery)
                    }
                })
            },
            allDataSize = getSiblingsSize(mediaId),
            observeChanges = { contentObserver.createQuery(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI) }
        )
    }

    override fun canShowSiblings(mediaId: MediaId): Boolean {
        return getSiblingsSize(mediaId) > 0
    }

    override fun getRelatedArtistsSize(mediaId: MediaId): Int {
        val cursor = playlistQueries.getRelatedArtists(mediaId.categoryId, null)
        return contentResolver.queryCountRow(cursor)
    }

    override fun getRelatedArtistsChunk(mediaId: MediaId): ChunkedData<Artist> {
        if (PlaylistGateway.isAutoPlaylist(mediaId.categoryId)){
            // auto playlist has not related artists
            return ChunkedData({ listOf<Artist>() }, 0, { flowOf() })
        }

        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = playlistQueries.getRelatedArtists(mediaId.categoryId, chunkRequest)
                contentResolver.queryAll(
                    cursor,
                    { it.toArtist() },
                    { ArtistRepository.updateImages(it, usedImageGateway) })
            },
            allDataSize = getRelatedArtistsSize(mediaId),
            observeChanges = { contentObserver.createQuery(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI) }
        )
    }

    override fun canShowRelatedArtists(mediaId: MediaId): Boolean {
        if (PlaylistGateway.isAutoPlaylist(mediaId.categoryId)){
            return false
        }
        return getRelatedArtistsSize(mediaId) > 0 && prefsGateway.getVisibleTabs()[2]
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

    override fun getMostPlayedChunk(mediaId: MediaId): ChunkedData<Song> {
        val maxAllowed = 10
        if (PlaylistGateway.isAutoPlaylist(mediaId.categoryId)) {
            return ChunkedData(
                chunkOf = { listOf<Song>() },
                allDataSize = getMostPlayedSize(mediaId),
                observeChanges = { flowOf() }
            )
        }

        return ChunkedData(
            chunkOf = { chunkRequest ->
                val mostPlayed = mostPlayedDao.query(mediaId.categoryId, maxAllowed)
                val cursor = playlistQueries.getExisting(mostPlayed.joinToString { "'${it.songId}'" })
                val existings = contentResolver.queryAll(cursor, { it.toSong() }, {
                    val result = SongRepository.adjustImages(context, it)
                    SongRepository.updateImages(result, usedImageGateway)
                })
                mostPlayed.asSequence()
                    .mapNotNull { mostPlayed -> existings.firstOrNull { it.id == mostPlayed.songId } }
                    .take(maxAllowed)
                    .toList()
            },
            allDataSize = clamp(mostPlayedDao.count(mediaId.categoryId), 0, maxAllowed),
            observeChanges = {
                mostPlayedDao.observe(mediaId.categoryId, maxAllowed)
                    .asFlow()
                    .drop(1)
                    .map { Unit }
            }
        )
    }

    override fun getPlaylistsBlocking(): List<Playlist> {
        val cursor = playlistQueries.getAll(null)
        return contentResolver.queryAll(cursor, { it.toPlaylist(context, 0) }, { playlistList ->
            playlistList.map { playlist ->
                // get the size for every playlist
                val sizeQueryCursor = playlistQueries.countPlaylistSize(playlist.id)
                val sizeQuery = contentResolver.queryCountRow(sizeQueryCursor)
                playlist.copy(size = sizeQuery)
            }
        })
    }

    override fun insertMostPlayed(mediaId: MediaId): Completable {
        return Completable.complete()
//        val songId = mediaId.leaf!!
//        val playlistId = mediaId.categoryValue.toLong()
//        songGateway.getByParam(songId).asFlowable()
//            .firstOrError()
//            .flatMapCompletable { song ->
//                CompletableSource { mostPlayedDao.insertOne(PlaylistMostPlayedEntity(0, song.id, playlistId)) }
//            }
    }
}