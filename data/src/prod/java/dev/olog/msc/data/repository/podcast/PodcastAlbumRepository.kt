package dev.olog.msc.data.repository.podcast

import android.content.Context
import android.provider.MediaStore
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.ChunkedData
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.gateway.PodcastAlbumGateway
import dev.olog.msc.core.gateway.PodcastGateway
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.mapper.toPodcastAlbum
import dev.olog.msc.data.repository.queries.AlbumQueries
import dev.olog.msc.data.repository.util.CommonQuery
import dev.olog.msc.data.repository.util.ContentObserver
import dev.olog.msc.imageprovider.ImagesFolderUtils
import dev.olog.msc.shared.utils.clamp
import io.reactivex.Completable
import io.reactivex.Observable
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.flow.asFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

internal class PodcastAlbumRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    appDatabase: AppDatabase,
    private val podcastGateway: PodcastGateway,
    private val usedImageGateway: UsedImageGateway,
    private val prefsGateway: AppPreferencesGateway,
    private val contentObserver: ContentObserver

) : PodcastAlbumGateway {

    private val albumQueries = AlbumQueries(prefsGateway, true)

    private val lastPlayedDao = appDatabase.lastPlayedPodcastAlbumDao()

    private suspend fun queryAll(): Flow<List<PodcastAlbum>> {
        return flowOf()
    }

    private suspend fun querySingle(selection: String, args: Array<String>): Flow<PodcastAlbum> {
        return flow {  }
    }

    private fun getAllSize(): Int {
        val cursor = albumQueries.size(context, prefsGateway.getBlackList())
        return CommonQuery.sizeQuery(cursor)
    }

    override fun getChunk(): ChunkedData<PodcastAlbum> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = albumQueries.all(context, prefsGateway.getBlackList(), chunkRequest)
                CommonQuery.query(cursor, { it.toPodcastAlbum() }, { updateImages(it) })
            },
            allDataSize = getAllSize(),
            observeChanges = { contentObserver.createQuery(AlbumQueries.MEDIA_STORE_URI) }
        )
    }

    private fun updateImages(list: List<PodcastAlbum>): List<PodcastAlbum> {
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

    override suspend fun getAll(): Flow<List<PodcastAlbum>> {
        return queryAll()
    }

    override suspend fun getByParam(param: Long): Flow<PodcastAlbum> {
        return querySingle("${MediaStore.Audio.Media.ALBUM_ID} = ?", arrayOf(param.toString()))
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observePodcastListByParam(albumId: Long): Observable<List<Podcast>> = runBlocking {
        podcastGateway.getAll().asObservable()
            .map { it.filter { it.albumId == albumId } }
    }

    override fun observeByArtist(artistId: Long): Observable<List<PodcastAlbum>> = runBlocking {
        getAll().asObservable()
            .map { it.filter { it.artistId == artistId } }
    }

    override fun getLastPlayedChunk(): ChunkedData<PodcastAlbum> {
        val maxAllowed = 10
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val lastPlayed = lastPlayedDao.getAll(maxAllowed)
                val existingLastCursor =
                    albumQueries.existingLastPlayed(context, prefsGateway.getBlackList(), lastPlayed.joinToString { "'${it.id}'" })
                val existingLastPlayed = CommonQuery.query(existingLastCursor, { it.toPodcastAlbum() }, { updateImages(it) })
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

    override fun getRecentlyAddedChunk(): ChunkedData<PodcastAlbum> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = albumQueries.recentlyAdded(context, prefsGateway.getBlackList(), chunkRequest)
                CommonQuery.query(cursor, { it.toPodcastAlbum() }, { updateImages(it) })
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