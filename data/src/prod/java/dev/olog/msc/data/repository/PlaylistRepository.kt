package dev.olog.msc.data.repository

import android.content.Context
import android.provider.MediaStore
import com.squareup.sqlbrite3.BriteContentResolver
import com.squareup.sqlbrite3.SqlBrite
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.PrefsKeys
import dev.olog.msc.core.coroutines.debounceFirst
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.ChunkRequest
import dev.olog.msc.core.entity.ChunkedData
import dev.olog.msc.core.entity.track.Playlist
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.FavoriteGateway
import dev.olog.msc.core.gateway.PlaylistGateway
import dev.olog.msc.core.gateway.SongGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.PlaylistMostPlayedEntity
import dev.olog.msc.data.mapper.toPlaylist
import dev.olog.msc.data.mapper.toPlaylistSong
import dev.olog.msc.data.repository.queries.PlaylistQueries
import dev.olog.msc.data.repository.util.CommonQuery
import dev.olog.msc.data.repository.util.ContentObserver
import dev.olog.msc.data.repository.util.ContentResolverFlow
import dev.olog.msc.shared.emitOnlyWithStoragePermission
import dev.olog.msc.shared.onlyWithStoragePermission
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.asFlowable
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

private val SONG_PROJECTION = arrayOf(
    MediaStore.Audio.Playlists.Members._ID,
    MediaStore.Audio.Playlists.Members.AUDIO_ID
)
private val SONG_SELECTION = null
private val SONG_SELECTION_ARGS: Array<String>? = null
private const val SONG_SORT_ORDER = MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER

internal class PlaylistRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val rxContentResolver: BriteContentResolver,
    private val songGateway: SongGateway,
    private val favoriteGateway: FavoriteGateway,
    appDatabase: AppDatabase,
    private val helper: PlaylistRepositoryHelper,
    private val appPrefsUseCase: AppPreferencesGateway,
    prefsKeys: PrefsKeys,
    private val contentResolver: ContentResolverFlow,
    private val contentObserver: ContentObserver

) : PlaylistGateway {

    private val playlistQueries = PlaylistQueries()

    private val resources = context.resources

    private val mostPlayedDao = appDatabase.playlistMostPlayedDao()
    private val historyDao = appDatabase.historyDao()

    private val autoPlaylistTitles = resources.getStringArray(prefsKeys.autoPlaylist())

    private fun createAutoPlaylist(id: Long, title: String): Playlist {
        return Playlist(id, title, 0, "")
    }

    private suspend fun queryAll(): Flow<List<Playlist>> {
        return contentResolver.createQuery<Playlist>(
            PlaylistQueries.MEDIA_STORE_URI,
            PlaylistQueries.PROJECTION,
            null,
            null,
            PlaylistQueries.SORT_ORDER,
            false
        ).mapToList {
//            val id = it.extractId()
//            val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", id)
//            val size = CommonQuery.getSize(context.contentResolver, uri)
            it.toPlaylist(context, 0)
        }.emitOnlyWithStoragePermission()
            .adjust()
            .distinctUntilChanged()
    }

    private suspend fun querySingle(selection: String, args: Array<String>): Flow<Playlist> {
        return contentResolver.createQuery<Playlist>(
            PlaylistQueries.MEDIA_STORE_URI,
            PlaylistQueries.PROJECTION, selection,
            args,
            PlaylistQueries.SORT_ORDER,
            false
        ).mapToList {
//            val id = it.extractId()
//            val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", id)
//            val size = CommonQuery.getSize(context.contentResolver, uri)
            it.toPlaylist(context, 0)
        }.emitOnlyWithStoragePermission()
            .adjust()
            .map { it.first() }
            .distinctUntilChanged()
    }

    override fun getChunk(): ChunkedData<Playlist> {
        return ChunkedData(
            chunkOf = { chunkRequest -> makeChunkOf(chunkRequest) },
            allDataSize = CommonQuery.sizeQuery(playlistQueries.size(context)),
            observeChanges = { contentObserver.createQuery(PlaylistQueries.MEDIA_STORE_URI) }
        )
    }

    private fun makeChunkOf(chunkRequest: ChunkRequest): List<Playlist> {
        return CommonQuery.query(
            cursor = playlistQueries.all(context, chunkRequest),
            mapper = { it.toPlaylist(context, 0) },
            afterQuery = { playlistList ->
                val blackList = appPrefsUseCase.getBlackList()
                playlistList.map { playlist ->
                    // get the size for every playlist
                    val sizeQueryCursor = playlistQueries.playlistSize(context, playlist.id, blackList)
                    val sizeQuery = CommonQuery.sizeQuery(sizeQueryCursor)
                    playlist.copy(size = sizeQuery)
                }
            })
    }

    private inline fun Flow<List<Playlist>>.adjust(): Flow<List<Playlist>> {
        return this.debounceFirst(100)
            .map { removeBlacklisted(it.toMutableList()) }
    }

    private fun removeBlacklisted(list: MutableList<Playlist>): List<Playlist> {
        val songsIds = CommonQuery.getAllSongsIdNotBlackListd(context.contentResolver, appPrefsUseCase)
        for (playlist in list.toList()) {
            val newSize = calculateNewPlaylistSize(playlist.id, songsIds)
            list[list.indexOf(playlist)] = playlist.copy(size = newSize)
        }
        return list
    }

    private fun calculateNewPlaylistSize(id: Long, songIds: List<Long>): Int {
        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", id)
        val cursor =
            context.contentResolver.query(uri, arrayOf(MediaStore.Audio.Playlists.Members.AUDIO_ID), null, null, null)
        val list = mutableListOf<Long>()
        while (cursor != null && cursor.moveToNext()) {
            list.add(cursor.getLong(0))
        }
        cursor?.close()
        list.retainAll(songIds)

        return list.size
    }

    override suspend fun getAll(): Flow<List<Playlist>> = queryAll()

    override suspend fun getByParam(param: Long): Flow<Playlist> {
        if (PlaylistGateway.isAutoPlaylist(param)) {
            // TODO improve
//            return getAllAutoPlaylists().map { list -> list.first { it.id == param } }
            return flowOf(getAllAutoPlaylists().first { it.id == param })
        }

        return querySingle("${MediaStore.Audio.Playlists._ID} = ?", arrayOf(param.toString()))
    }


    override fun getAllAutoPlaylists(): List<Playlist> {
        return listOf(
            createAutoPlaylist(PlaylistGateway.LAST_ADDED_ID, autoPlaylistTitles[0]),
            createAutoPlaylist(PlaylistGateway.FAVORITE_LIST_ID, autoPlaylistTitles[1]),
            createAutoPlaylist(PlaylistGateway.HISTORY_LIST_ID, autoPlaylistTitles[2])
        )
    }

    override fun insertSongToHistory(songId: Long): Completable {
        return historyDao.insert(songId)
    }

    override fun getPlaylistsBlocking(): List<Playlist> {
        val cursor = context.contentResolver.query(
            PlaylistQueries.MEDIA_STORE_URI,
            PlaylistQueries.PROJECTION,
            null, null,
            PlaylistQueries.SORT_ORDER
        )
        val list = mutableListOf<Playlist>()
        while (cursor != null && cursor.moveToNext()) {
            val playlist = cursor.toPlaylist(context, -1)
            list.add(playlist)
        }
        cursor?.close()
        return list
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(playlistId: Long): Observable<List<Song>> {
        return when (playlistId) {
            PlaylistGateway.LAST_ADDED_ID -> getLastAddedSongs()
            PlaylistGateway.FAVORITE_LIST_ID -> runBlocking { favoriteGateway.getAll().asObservable() }
            PlaylistGateway.HISTORY_LIST_ID -> runBlocking { historyDao.getAllAsSongs(songGateway.getAll().asFlowable().firstOrError()) }
            else -> getPlaylistSongs(playlistId)
        }
    }

    private fun getLastAddedSongs(): Observable<List<Song>> = runBlocking {
        songGateway.getAll().asObservable().switchMapSingle {
            it.toFlowable().toSortedList { o1, o2 -> (o2.dateAdded - o1.dateAdded).toInt() }
        }
    }

    private fun getPlaylistSongs(playlistId: Long): Observable<List<Song>> = runBlocking {
        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)

        rxContentResolver.createQuery(
            uri, SONG_PROJECTION, SONG_SELECTION,
            SONG_SELECTION_ARGS, SONG_SORT_ORDER, false
        ).onlyWithStoragePermission()
            .lift(SqlBrite.Query.mapToList { it.toPlaylistSong() })
            .switchMapSingle { playlistSongs ->
                runBlocking {
                    songGateway.getAll().asObservable().firstOrError().map { songs ->
                        playlistSongs.asSequence()
                            .mapNotNull { playlistSong ->
                                val song = songs.firstOrNull { it.id == playlistSong.songId }
                                song?.copy(trackNumber = playlistSong.idInPlaylist.toInt())
                            }.toList()
                    }
                }
            }
    }

    override fun getMostPlayed(mediaId: MediaId): Observable<List<Song>> = runBlocking {
        val playlistId = mediaId.categoryValue.toLong()
        if (PlaylistGateway.isAutoPlaylist(playlistId)) {
            Observable.just(listOf())
        } else {
            mostPlayedDao.getAll(playlistId, songGateway.getAll().asObservable())
        }
    }

    override fun insertMostPlayed(mediaId: MediaId): Completable = runBlocking {
        val songId = mediaId.leaf!!
        val playlistId = mediaId.categoryValue.toLong()
        songGateway.getByParam(songId).asFlowable()
            .firstOrError()
            .flatMapCompletable { song ->
                CompletableSource { mostPlayedDao.insertOne(PlaylistMostPlayedEntity(0, song.id, playlistId)) }
            }
    }

    override fun deletePlaylist(playlistId: Long): Completable {
        return helper.deletePlaylist(playlistId)
    }

    override fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>): Completable {
        return Completable.fromCallable { helper.addSongsToPlaylist(playlistId, songIds) }
    }


    override fun clearPlaylist(playlistId: Long): Completable {
        return helper.clearPlaylist(playlistId)
    }

    override fun removeFromPlaylist(playlistId: Long, idInPlaylist: Long): Completable {
        return helper.removeSongFromPlaylist(playlistId, idInPlaylist)
    }

    override fun createPlaylist(playlistName: String): Single<Long> {
        return helper.createPlaylist(playlistName)
    }

    override fun renamePlaylist(playlistId: Long, newTitle: String): Completable {
        return helper.renamePlaylist(playlistId, newTitle)
    }

    override fun moveItem(playlistId: Long, from: Int, to: Int): Boolean {
        return helper.moveItem(playlistId, from, to)
    }

    override fun removeDuplicated(playlistId: Long): Completable {
        return Completable.fromCallable { helper.removeDuplicated(playlistId) }
    }
}