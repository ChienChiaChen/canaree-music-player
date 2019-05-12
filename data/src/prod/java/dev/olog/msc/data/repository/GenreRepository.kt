package dev.olog.msc.data.repository

import android.content.Context
import android.provider.BaseColumns
import android.provider.MediaStore
import com.squareup.sqlbrite3.BriteContentResolver
import com.squareup.sqlbrite3.SqlBrite
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.track.Genre
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.GenreGateway
import dev.olog.msc.core.gateway.SongGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.GenreMostPlayedEntity
import dev.olog.msc.data.mapper.extractId
import dev.olog.msc.data.mapper.toGenre
import dev.olog.msc.data.repository.util.CommonQuery
import dev.olog.msc.data.repository.util.ContentResolverFlow
import dev.olog.msc.shared.emitOnlyWithStoragePermission
import dev.olog.msc.core.coroutines.debounceFirst
import dev.olog.msc.core.entity.ChunkRequest
import dev.olog.msc.core.entity.ChunkedData
import dev.olog.msc.data.repository.queries.GenreQueries
import dev.olog.msc.data.repository.util.ContentObserver
import dev.olog.msc.shared.extensions.debounceFirst
import dev.olog.msc.shared.onlyWithStoragePermission
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Observable
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

private val SONG_PROJECTION = arrayOf(BaseColumns._ID)
private val SONG_SELECTION = null
private val SONG_SELECTION_ARGS: Array<String>? = null
private const val SONG_SORT_ORDER = "lower(${MediaStore.Audio.Genres.Members.TITLE})"

internal class GenreRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val rxContentResolver: BriteContentResolver,
    private val songGateway: SongGateway,
    private val appPrefsUseCase: AppPreferencesGateway,
    appDatabase: AppDatabase,
    private val contentObserver: ContentObserver

) : GenreGateway {

    private val genreQueries = GenreQueries()

    private val mostPlayedDao = appDatabase.genreMostPlayedDao()

    private suspend fun queryAll(): Flow<List<Genre>> {
        return flowOf()
//        return contentResolver.createQuery<Genre>(
//            MEDIA_STORE_URI, PROJECTION, null,
//            null, SORT_ORDER, true
//        ).mapToList {
//            val id = it.extractId()
//            val uri = MediaStore.Audio.Genres.Members.getContentUri("external", id)
//            val size = CommonQuery.getSize(context.contentResolver, uri)
//            it.toGenre(context, 0)
//        }.emitOnlyWithStoragePermission()
//            .adjust()
//            .distinctUntilChanged()
    }

    private suspend fun querySingle(selection: String, args: Array<String>): Flow<Genre> {
        return flow {  }
//        return contentResolver.createQuery<Genre>(
//            MEDIA_STORE_URI, PROJECTION, selection,
//            args, SORT_ORDER, true
//        ).mapToList {
//            val id = it.extractId()
//            val uri = MediaStore.Audio.Genres.Members.getContentUri("external", id)
//            val size = CommonQuery.getSize(context.contentResolver, uri)
//            it.toGenre(context, 0)
//        }.emitOnlyWithStoragePermission()
//            .adjust()
//            .map { it.first() }
//            .distinctUntilChanged()
    }

    override fun getChunk(): ChunkedData<Genre> {
        return ChunkedData(
            chunkOf = { chunkRequest -> makeChunkOf(chunkRequest) },
            allDataSize = CommonQuery.sizeQuery(genreQueries.size(context)),
            observeChanges = { contentObserver.createQuery(GenreQueries.MEDIA_STORE_URI) }
        )
    }

    private fun makeChunkOf(chunkRequest: ChunkRequest): List<Genre> {
        return CommonQuery.query(
            cursor = genreQueries.all(context, chunkRequest),
            mapper = { it.toGenre(context, 0) },
            afterQuery = { genreList ->
                val blackList = appPrefsUseCase.getBlackList()
                genreList.map { genre ->
                    // get the size for every playlist
                    val sizeQueryCursor = genreQueries.genreSize(context, genre.id, blackList)
                    val sizeQuery = CommonQuery.sizeQuery(sizeQueryCursor)
                    genre.copy(size = sizeQuery)
                }
            })
    }

    override suspend fun getAll(): Flow<List<Genre>> {
        return queryAll()
    }

    override suspend fun getByParam(param: Long): Flow<Genre> {
        return querySingle("${MediaStore.Audio.Genres._ID} = ?", arrayOf(param.toString()))
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(genreId: Long): Observable<List<Song>> = runBlocking{
        val uri = MediaStore.Audio.Genres.Members.getContentUri("external", genreId)

        rxContentResolver.createQuery(
            uri, SONG_PROJECTION,
            SONG_SELECTION,
            SONG_SELECTION_ARGS,
            SONG_SORT_ORDER,
            false
        ).onlyWithStoragePermission()
            .debounceFirst()
            .lift(SqlBrite.Query.mapToList { it.extractId() })
            .switchMapSingle { ids ->
                runBlocking {
                    songGateway.getAll().asObservable().firstOrError().map { songs ->
                        ids.asSequence()
                            .mapNotNull { id -> songs.firstOrNull { it.id == id } }
                            .toList()
                    }
                }
            }.distinctUntilChanged()
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun getMostPlayed(mediaId: MediaId): Observable<List<Song>> = runBlocking{
        val genreId = mediaId.categoryValue.toLong()
        mostPlayedDao.getAll(genreId, songGateway.getAll().asObservable())
    }

    override fun insertMostPlayed(mediaId: MediaId): Completable = runBlocking{
        val songId = mediaId.leaf!!
        val genreId = mediaId.categoryValue.toLong()
        songGateway.getByParam(songId).asObservable()
            .firstOrError()
            .flatMapCompletable { song ->
                CompletableSource { mostPlayedDao.insertOne(GenreMostPlayedEntity(0, song.id, genreId)) }
            }
    }

}