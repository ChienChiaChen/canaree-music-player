package dev.olog.msc.data.repository

import android.content.Context
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media.DATA
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.ChunkedData
import dev.olog.msc.core.entity.track.Folder
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.FolderGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.mapper.toFolder
import dev.olog.msc.data.repository.queries.FolderQueries
import dev.olog.msc.data.repository.util.CommonQuery
import dev.olog.msc.data.repository.util.ContentObserver
import dev.olog.msc.data.repository.util.ContentResolverFlow
import io.reactivex.Completable
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

internal class FolderRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val contentResolverFlow: ContentResolverFlow,
    private val contentObserver: ContentObserver,
    appDatabase: AppDatabase,
    private val appPrefsUseCase: AppPreferencesGateway

) : FolderGateway {

    private val folderQueries = FolderQueries()

    private val mostPlayedDao = appDatabase.folderMostPlayedDao()

    private suspend fun queryAll(): Flow<List<Folder>> {
        return contentResolverFlow.createQuery<Folder>(
            FolderQueries.MEDIA_STORE_URI,
            arrayOf(
                " distinct substr($DATA, 1, length($DATA) - length(${MediaStore.MediaColumns.DISPLAY_NAME}) - 1) as folder, count(*) as songs"
            ),
            CommonQuery.getBlacklistedSelection(appPrefsUseCase.getBlackList(), FolderQueries.SELECTION, "group by (folder"),
            null,
            "lower(folder)",
            true
        ).mapToList { it.toFolder(context) }
    }

    override suspend fun getAll(): Flow<List<Folder>> {
        return queryAll()
    }

    override suspend fun getByParam(param: String): Flow<Folder> {
        // TODO find a better way
        return queryAll().map { list -> list.first { it.path == param } }
    }

    override fun getChunk(): ChunkedData<Folder> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = folderQueries.all(context,appPrefsUseCase.getBlackList(), chunkRequest)
                CommonQuery.query(cursor, { it.toFolder(context) }, null)
            },
            allDataSize = CommonQuery.sizeQuery(folderQueries.size(context,appPrefsUseCase.getBlackList())),
            observeChanges = { contentObserver.createQuery(FolderQueries.MEDIA_STORE_URI) }
        )
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(path: String): Observable<List<Song>> = runBlocking {
//        songGateway.getAll().asObservable().map { list ->
//            list.asSequence().filter { it.folderPath == path }.toList()
//        }.distinctUntilChanged()
        Observable.just(listOf<Song>())
    }

    override fun getMostPlayed(mediaId: MediaId): Observable<List<Song>> = runBlocking {
//        val folderPath = mediaId.categoryValue
//        mostPlayedDao.getAll(folderPath, songGateway.getAll().asObservable())
        Observable.just(listOf<Song>())
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