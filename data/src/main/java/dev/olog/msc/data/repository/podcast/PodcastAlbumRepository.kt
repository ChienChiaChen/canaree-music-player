package dev.olog.msc.data.repository.podcast

import android.content.Context
import android.provider.MediaStore
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.ChunkedData
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.msc.core.gateway.podcast.PodcastGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.mapper.toPodcast
import dev.olog.msc.data.mapper.toPodcastAlbum
import dev.olog.msc.data.repository.queries.AlbumQueries
import dev.olog.msc.data.repository.util.*
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
    private val contentObserver: ContentObserver,
    private val contentResolverFlow: ContentResolverFlow

) : PodcastAlbumGateway {

    companion object {
        internal fun updateImages(context: Context, list: List<PodcastAlbum>, usedImageGateway: UsedImageGateway): List<PodcastAlbum> {
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
    }

    private val contentResolver = context.contentResolver
    private val albumQueries = AlbumQueries(prefsGateway, true, contentResolver)

    private val lastPlayedDao = appDatabase.lastPlayedPodcastAlbumDao()

    override suspend fun getAll(): Flow<List<PodcastAlbum>> {
        return flowOf()
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observePodcastListByParam(albumId: Long): Observable<List<Podcast>> = runBlocking {
        podcastGateway.getAll().asObservable().map { it.filter { it.albumId == albumId } }
    }

    private fun getAllSize(): Int {
        val cursor = albumQueries.countAll()
        return contentResolver.querySize(cursor)
    }

    override fun getChunk(): ChunkedData<PodcastAlbum> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = albumQueries.getAll(chunkRequest)
                contentResolver.queryAll(cursor, { it.toPodcastAlbum() }, { updateImages(context, it, usedImageGateway) })
            },
            allDataSize = getAllSize(),
            observeChanges = { contentObserver.createQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) }
        )
    }

    override fun getByParam(param: Long): PodcastAlbum {
        return contentResolver.querySingle(albumQueries.getById(param), { it.toPodcastAlbum() }, {
            updateImages(context, listOf(it), usedImageGateway).first()
        })
    }

    override suspend fun observeByParam(param: Long): Flow<PodcastAlbum> {
        return contentResolverFlow.createQuery<PodcastAlbum>({ albumQueries.getById(param) }, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            .mapToOne { it.toPodcastAlbum() }
            .map { updateImages(context, listOf(it), usedImageGateway).first() }
            .distinctUntilChanged()
    }

    override fun observeByArtist(artistId: Long): Observable<List<PodcastAlbum>> = runBlocking {
        getAll().asObservable().map { it.filter { it.artistId == artistId } }
    }

    override fun getLastPlayedChunk(): ChunkedData<PodcastAlbum> {
        val maxAllowed = 10
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val lastPlayed = lastPlayedDao.getAll(maxAllowed)
                val existingLastCursor =
                    albumQueries.getExistingLastPlayed(lastPlayed.joinToString { "'${it.id}'" })
                val existingLastPlayed = contentResolver.queryAll(existingLastCursor, { it.toPodcastAlbum() }, {
                    updateImages(context, it, usedImageGateway)
                })
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
                val cursor = albumQueries.getRecentlyAddedAlbums(chunkRequest)
                contentResolver.queryAll(cursor, { it.toPodcastAlbum() }, { updateImages(context, it, usedImageGateway) })
            },
            allDataSize = contentResolver.queryCountRow(albumQueries.getRecentlyAddedAlbums(null)),
            observeChanges = { contentObserver.createQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) }
        )
    }

    override fun getPodcastListByParamChunk(param: Long): ChunkedData<Podcast> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = albumQueries.getSongList(param, chunkRequest)
                contentResolver.queryAll(cursor, { it.toPodcast() }, {
                    PodcastRepository.adjustImages(context, it)
                })
            },
            allDataSize = contentResolver.querySize(albumQueries.countSongList(param)),
            observeChanges = { contentObserver.createQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) }
        )
    }

    override fun getPodcastListByParamDuration(param: Long): Int {
        return contentResolver.querySize(albumQueries.getSongListDuration(param))
    }

    private fun getSiblingsSize(mediaId: MediaId): Int {
        return contentResolver.queryCountRow(albumQueries.getSiblingsChunk(mediaId.categoryId, null))
    }

    override fun getSiblingsChunk(mediaId: MediaId): ChunkedData<PodcastAlbum> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = albumQueries.getSiblingsChunk(mediaId.categoryId, chunkRequest)
                contentResolver.queryAll(cursor, { it.toPodcastAlbum() }, { updateImages(context, it, usedImageGateway) })
            },
            allDataSize = getSiblingsSize(mediaId),
            observeChanges = { contentObserver.createQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) }
        )
    }

    override fun canShowSiblings(mediaId: MediaId): Boolean {
        return getSiblingsSize(mediaId) > 0
    }

    override fun canShowRecentlyAdded(): Boolean {
        val cursor = albumQueries.getRecentlyAddedAlbums(null)
        val size = contentResolver.queryCountRow(cursor)
        return prefsGateway.canShowLibraryNewVisibility() && size > 0
    }

    override fun addLastPlayed(id: Long): Completable {
        return lastPlayedDao.insertOne(id)
    }
}