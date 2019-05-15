package dev.olog.msc.data.repository

import android.content.Context
import android.provider.MediaStore
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.ChunkedData
import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.entity.track.Folder
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.track.FolderGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.mapper.toArtist
import dev.olog.msc.data.mapper.toFolder
import dev.olog.msc.data.mapper.toSong
import dev.olog.msc.data.repository.queries.FolderQueries
import dev.olog.msc.data.repository.util.*
import dev.olog.msc.shared.utils.clamp
import io.reactivex.Completable
import io.reactivex.Observable
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.flow.asFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

internal class FolderRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val contentObserver: ContentObserver,
    appDatabase: AppDatabase,
    private val prefsGateway: AppPreferencesGateway,
    private val usedImageGateway: UsedImageGateway,
    private val contentResolverFlow: ContentResolverFlow

) : FolderGateway {

    private val contentResolver = context.contentResolver
    private val folderQueries = FolderQueries(prefsGateway, contentResolver)

    private val mostPlayedDao = appDatabase.folderMostPlayedDao()

    override suspend fun getAll(): Flow<List<Folder>> {
        return flowOf()
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(path: String): Observable<List<Song>> = runBlocking {
        //        songGateway.getAll().asObservable().map { list ->
//            list.asSequence().filter { it.folderPath == path }.toList()
//        }.distinctUntilChanged()
        Observable.just(listOf<Song>())
    }

    override fun getChunk(): ChunkedData<Folder> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = folderQueries.getAll(chunkRequest)
                contentResolver.queryAll(cursor, { it.toFolder(context) }, null)
            },
            allDataSize = contentResolver.querySize(folderQueries.countAll()),
            observeChanges = { contentObserver.createQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) }
        )
    }

    override fun getByParam(param: String): Folder {
        return contentResolver.querySingle(folderQueries.getByPath(param), { it.toFolder(context) }, null)
    }

    override suspend fun observeByParam(param: String): Flow<Folder> {
        return contentResolverFlow.createQuery<Folder>({ folderQueries.getByPath(param) }, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            .mapToOne { it.toFolder(context) }
            .distinctUntilChanged()
    }


    override fun getSongListByParamChunk(param: String): ChunkedData<Song> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = folderQueries.getSongList(param, chunkRequest)
                contentResolver.queryAll(cursor, { it.toSong() }, {
                    val result = SongRepository.adjustImages(context, it)
                    SongRepository.updateImages(result, usedImageGateway)
                })
            },
            allDataSize = contentResolver.queryCountRow(folderQueries.getSongList(param, null)),
            observeChanges = { contentObserver.createQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) }
        )
    }

    override fun getSongListByParamDuration(param: String): Int {
        return contentResolver.querySize(folderQueries.getSongListDuration(param))
    }

    override fun getRecentlyAddedSongsSize(mediaId: MediaId): Int {
        val cursor = folderQueries.getRecentlyAddedSongs(mediaId.categoryValue, null)
        return contentResolver.queryCountRow(cursor)
    }

    override fun getRecentlyAddedSongsChunk(mediaId: MediaId): ChunkedData<Song> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = folderQueries.getRecentlyAddedSongs(mediaId.categoryValue, chunkRequest)
                contentResolver.queryAll(cursor, { it.toSong() }, {
                    val result = SongRepository.adjustImages(context, it)
                    SongRepository.updateImages(result, usedImageGateway)
                })
            },
            allDataSize = getRecentlyAddedSongsSize(mediaId),
            observeChanges = { contentObserver.createQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) }
        )
    }

    override fun canShowRecentlyAddedSongs(mediaId: MediaId): Boolean {
        return getRecentlyAddedSongsSize(mediaId) > 0 && prefsGateway.getVisibleTabs()[1]
    }

    private fun getSiblingsSize(mediaId: MediaId): Int {
        val cursor = folderQueries.getSiblingsChunk(mediaId.categoryValue, null)
        return contentResolver.queryCountRow(cursor)
    }

    override fun getSiblingsChunk(mediaId: MediaId): ChunkedData<Folder> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = folderQueries.getSiblingsChunk(mediaId.categoryValue, chunkRequest)
                contentResolver.queryAll(cursor, { it.toFolder(context) }, null)
            },
            allDataSize = getSiblingsSize(mediaId),
            observeChanges = { contentObserver.createQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) }
        )
    }

    override fun canShowSiblings(mediaId: MediaId): Boolean {
        return getSiblingsSize(mediaId) > 0
    }

    override fun getRelatedArtistsSize(mediaId: MediaId): Int {
        val cursor = folderQueries.getRelatedArtists(mediaId.categoryValue, null)
        return contentResolver.queryCountRow(cursor)
    }

    override fun getRelatedArtistsChunk(mediaId: MediaId): ChunkedData<Artist> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = folderQueries.getRelatedArtists(mediaId.categoryValue, chunkRequest)
                contentResolver.queryAll(
                    cursor,
                    { it.toArtist() },
                    { ArtistRepository.updateImages(it, usedImageGateway) })
            },
            allDataSize = getRelatedArtistsSize(mediaId),
            observeChanges = { contentObserver.createQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) }
        )
    }

    override fun canShowRelatedArtists(mediaId: MediaId): Boolean {
        return getRelatedArtistsSize(mediaId) > 0 && prefsGateway.getVisibleTabs()[2]
    }

    private fun getMostPlayedSize(mediaId: MediaId): Int {
        return mostPlayedDao.count(mediaId.categoryValue)
    }

    override fun canShowMostPlayed(mediaId: MediaId): Boolean {
        return getMostPlayedSize(mediaId) > 0 && prefsGateway.getVisibleTabs()[0]
    }

    override fun getMostPlayedChunk(mediaId: MediaId): ChunkedData<Song> {
        val maxAllowed = 10
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val mostPlayed = mostPlayedDao.query(mediaId.categoryValue, maxAllowed)
                val cursor = folderQueries.getExisting(mediaId.categoryValue, mostPlayed.joinToString { "'${it.songId}'" })
                val existings = contentResolver.queryAll(cursor, { it.toSong() }, {
                    val result = SongRepository.adjustImages(context, it)
                    SongRepository.updateImages(result, usedImageGateway)
                })
                mostPlayed.asSequence()
                    .mapNotNull { mostPlayed -> existings.firstOrNull { it.id == mostPlayed.songId }  }
                    .take(maxAllowed)
                    .toList()
            },
            allDataSize = clamp(mostPlayedDao.count(mediaId.categoryValue), 0, maxAllowed),
            observeChanges = {
                mostPlayedDao.observe(mediaId.categoryValue, maxAllowed)
                    .asFlow()
                    .drop(1)
                    .map { Unit }
            }
        )
    }

    override fun insertMostPlayed(mediaId: MediaId): Completable = runBlocking {
        //        val songId = mediaId.leaf!!
//        songGateway.getByParam(songId).asObservable()
//            .firstOrError()
//            .flatMapCompletable { song ->
//                CompletableSource { mostPlayedDao.insertOne(FolderMostPlayedEntity(0, song.id, song.folderPath)) }
//            }
        Completable.complete()
    }

    override fun getAllUnfiltered(): Observable<List<Folder>> {
//        return songGateway.getAllUnfiltered()
//            .map(this::mapToFolderList)
        return Observable.just(listOf())
    }

}