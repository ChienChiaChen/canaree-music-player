package dev.olog.msc.data.repository

import android.content.Context
import android.provider.MediaStore
import dev.olog.msc.core.coroutines.merge
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.ChunkedData
import dev.olog.msc.core.entity.track.Album
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.AlbumGateway
import dev.olog.msc.core.gateway.SongGateway
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.mapper.toAlbum
import dev.olog.msc.data.repository.queries.AlbumQueries
import dev.olog.msc.data.repository.util.CommonQuery
import dev.olog.msc.data.repository.util.ContentObserver
import dev.olog.msc.data.repository.util.ContentResolverFlow
import dev.olog.msc.imageprovider.ImagesFolderUtils
import dev.olog.msc.shared.emitOnlyWithStoragePermission
import dev.olog.msc.shared.utils.clamp
import io.reactivex.Completable
import io.reactivex.Observable
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.flow.asFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

internal class AlbumRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val songGateway: SongGateway,
    appDatabase: AppDatabase,
    private val usedImageGateway: UsedImageGateway,
    private val contentObserver: ContentObserver,
    private val prefsGateway: AppPreferencesGateway

) : AlbumGateway {

    private val albumQueries = AlbumQueries(prefsGateway, false)

    private val lastPlayedDao = appDatabase.lastPlayedAlbumDao()

    private fun getAllSize(): Int {
        val cursor = albumQueries.size(context, prefsGateway.getBlackList())
        return CommonQuery.sizeQuery(cursor)
    }

    override fun getChunk(): ChunkedData<Album> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = albumQueries.all(context, prefsGateway.getBlackList(), chunkRequest)
                CommonQuery.query(cursor, { it.toAlbum() }, { updateImages(it) })
            },
            allDataSize = getAllSize(),
            observeChanges = {
                contentObserver.createQuery(AlbumQueries.MEDIA_STORE_URI)
                    .merge(prefsGateway.observeAllAlbumsSortOrder().drop(1)) // ignores emission on subscribe
            }
        )
    }

    private suspend fun queryAll(): Flow<List<Album>> {
        return flowOf()
//        return contentResolverFlow.createQuery<List<Album>>(
//            MEDIA_STORE_URI, arrayOf("count(*) as size"), null,
//            null, " size ASC LIMIT 1", true
//        ).mapToOne { listOf() }
//            .emitOnlyWithStoragePermission()
//            .adjust()
//            .distinctUntilChanged()
    }

    private suspend fun querySingle(selection: String, args: Array<String>): Flow<Album> {
        return flow<Album> {  }
//        return contentResolverFlow.createQuery<List<Album>>(
//            MEDIA_STORE_URI, arrayOf("count(*) as size"), selection,
//            args, " size ASC LIMIT 1", true
//        ).mapToOne { listOf() }
//            .emitOnlyWithStoragePermission()
//            .adjust()
//            .map { it.first() }
//            .distinctUntilChanged()
    }

    private fun updateImages(list: List<Album>): List<Album> {
        // TODO chech if has to call songrepository.adjustImages
        val allForAlbum = usedImageGateway.getAllForAlbums()
        if (allForAlbum.isEmpty()) {
            return list
        }

        return list.map { album ->
            val image =
                allForAlbum.firstOrNull { it.id == album.id }?.image ?: ImagesFolderUtils.forAlbum(context, album.id)
            album.copy(image = image)
        }
    }

    override suspend fun getAll(): Flow<List<Album>> {
        return queryAll()
    }

    override suspend fun getByParam(param: Long): Flow<Album> {
        return querySingle("${MediaStore.Audio.Media.ALBUM_ID} = ?", arrayOf(param.toString()))
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(albumId: Long): Observable<List<Song>> = runBlocking {
        songGateway.getAll().asObservable().map { it.filter { it.albumId == albumId } }
    }

    override fun observeByArtist(artistId: Long): Observable<List<Album>> = runBlocking {
        getAll().asObservable().map { it.filter { it.artistId == artistId } }
    }

    override fun getLastPlayedChunk(): ChunkedData<Album> {
        val maxAllowed = 10
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val lastPlayed = lastPlayedDao.getAll(maxAllowed)
                val existingLastCursor =
                    albumQueries.existingLastPlayed(context, prefsGateway.getBlackList(), lastPlayed.joinToString { "'${it.id}'" })
                val existingLastPlayed = CommonQuery.query(existingLastCursor, { it.toAlbum() }, { updateImages(it) })
                lastPlayed.asSequence()
                    .mapNotNull { last -> existingLastPlayed.firstOrNull { it.id == last.id } }
                    .take(maxAllowed)
                    .toList()
            },
            allDataSize = clamp(lastPlayedDao.getCount(), 0, maxAllowed),
            observeChanges = {
                lastPlayedDao.observeAll(1)
                    .asFlow()
                    .drop(1)
                    .map { Unit }
            }
        )
    }

    override fun canShowLastPlayed(): Boolean {
        return prefsGateway.canShowLibraryRecentPlayedVisibility() &&
                getAllSize() >= 5 &&
                lastPlayedDao.getCount() > 0
    }

    override fun getRecentlyAddedChunk(): ChunkedData<Album> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = albumQueries.recentlyAdded(context, prefsGateway.getBlackList(), chunkRequest)
                CommonQuery.query(cursor, { it.toAlbum() }, { updateImages(it) })
            },
            allDataSize = CommonQuery.sizeQuery(albumQueries.recentlyAdded(context, prefsGateway.getBlackList(), null)),
            observeChanges = { contentObserver.createQuery(AlbumQueries.MEDIA_STORE_URI) }
        )
    }

    override fun canShowRecentlyAdded(): Boolean {
        val cursor = albumQueries.recentlyAdded(context, prefsGateway.getBlackList(), null)
        val size = CommonQuery.sizeQuery(cursor)
        return prefsGateway.canShowLibraryNewVisibility() && size > 0
    }

    override fun addLastPlayed(id: Long): Completable {
        return lastPlayedDao.insertOne(id)
    }
}