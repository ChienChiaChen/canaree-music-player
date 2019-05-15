package dev.olog.msc.data.repository.podcast

import android.content.Context
import android.provider.MediaStore
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.ChunkedData
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.entity.podcast.PodcastArtist
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.core.gateway.podcast.PodcastArtistGateway
import dev.olog.msc.core.gateway.podcast.PodcastGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.mapper.toPodcast
import dev.olog.msc.data.mapper.toPodcastAlbum
import dev.olog.msc.data.mapper.toPodcastArtist
import dev.olog.msc.data.repository.queries.ArtistQueries
import dev.olog.msc.data.repository.util.*
import dev.olog.msc.shared.utils.clamp
import io.reactivex.Completable
import io.reactivex.Observable
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

internal class PodcastArtistRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    appDatabase: AppDatabase,
    private val podcastGateway: PodcastGateway,
    private val usedImageGateway: UsedImageGateway,
    private val prefsGateway: AppPreferencesGateway,
    private val contentObserver: ContentObserver,
    private val contentResolverFlow: ContentResolverFlow

) : PodcastArtistGateway {

    companion object {
        private fun updateImages(list: List<PodcastArtist>, usedImageGateway: UsedImageGateway): List<PodcastArtist> {
            val allForArtists = usedImageGateway.getAllForArtists()
            if (allForArtists.isEmpty()) {
                return list
            }
            return list.map { artist ->
                val image = allForArtists.firstOrNull { it.id == artist.id }?.image ?: artist.image
                artist.copy(image = image)
            }
        }
    }

    private val contentResolver = context.contentResolver
    private val artistQueries = ArtistQueries(prefsGateway, true, contentResolver)

    private val lastPlayedDao = appDatabase.lastPlayedPodcastArtistDao()

    override suspend fun getAll(): Flow<List<PodcastArtist>> {
        return flowOf()
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observePodcastListByParam(artistId: Long): Observable<List<Podcast>> = runBlocking {
        podcastGateway.getAll().asObservable()
            .map { it.filter { it.artistId == artistId } }
    }

    private fun getAllSize(): Int {
        return contentResolver.querySize(artistQueries.countAll())
    }

    override fun getChunk(): ChunkedData<PodcastArtist> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = artistQueries.getAll(chunkRequest)
                contentResolver.queryAll(cursor, { it.toPodcastArtist() }, { updateImages(it, usedImageGateway) })
            },
            allDataSize = getAllSize(),
            observeChanges = { contentObserver.createQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) }
        )
    }

    override fun getByParam(param: Long): PodcastArtist {
        val cursor = artistQueries.getById(param)
        return contentResolver.querySingle(cursor, { it.toPodcastArtist() }, {
            updateImages(listOf(it), usedImageGateway).first()
        })
    }

    override suspend fun observeByParam(param: Long): Flow<PodcastArtist> {
        return contentResolverFlow.createQuery<PodcastArtist>({ artistQueries.getById(param) }, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            .mapToList { it.toPodcastArtist() }
            .map { updateImages(it, usedImageGateway).first() }
            .distinctUntilChanged()
    }

    override fun getLastPlayedChunk(): ChunkedData<PodcastArtist> {
        val maxAllowed = 10
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val lastPlayed = lastPlayedDao.getAll(maxAllowed)
                val existingLastCursor =
                    artistQueries.getExistingLastPlayed(lastPlayed.joinToString { "'${it.id}'" })
                val existingLastPlayed = contentResolver.queryAll(existingLastCursor, { it.toPodcastArtist() }, {
                    updateImages(it, usedImageGateway)
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

    override fun getRecentlyAddedChunk(): ChunkedData<PodcastArtist> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = artistQueries.getRecentlyAdded(chunkRequest)
                contentResolver.queryAll(cursor, { it.toPodcastArtist() }, { updateImages(it, usedImageGateway) })
            },
            allDataSize = contentResolver.queryCountRow(artistQueries.getRecentlyAdded(null)),
            observeChanges = { contentObserver.createQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) }
        )
    }

    override fun getPodcastListByParamChunk(param: Long): ChunkedData<Podcast> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = artistQueries.getSongList(param, chunkRequest)
                contentResolver.queryAll(cursor, { it.toPodcast() }, {
                    PodcastRepository.adjustImages(context, it)
                })
            },
            allDataSize = contentResolver.querySize(artistQueries.countSongList(param)),
            observeChanges = { contentObserver.createQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) }
        )
    }

    override fun getPodcastListByParamDuration(param: Long): Int {
        return contentResolver.querySize(artistQueries.getSongListDuration(param))
    }

    private fun getSiblingsSize(mediaId: MediaId): Int {
        return contentResolver.queryCountRow(artistQueries.getSiblingsChunk(mediaId.categoryId, null))
    }

    override fun getSiblingsChunk(mediaId: MediaId): ChunkedData<PodcastAlbum> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = artistQueries.getSiblingsChunk(mediaId.categoryId, chunkRequest)
                contentResolver.queryAll(cursor, { it.toPodcastAlbum() }, { PodcastAlbumRepository.updateImages(context, it, usedImageGateway) })
            },
            allDataSize = getSiblingsSize(mediaId),
            observeChanges = { contentObserver.createQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) }
        )
    }

    override fun canShowSiblings(mediaId: MediaId): Boolean {
        return getSiblingsSize(mediaId) > 0
    }

    override fun canShowRecentlyAdded(): Boolean {
        val cursor = artistQueries.getRecentlyAdded(null)
        return prefsGateway.canShowLibraryNewVisibility() &&
                contentResolver.queryCountRow(cursor) > 0
    }

    override fun addLastPlayed(id: Long): Completable {
        return lastPlayedDao.insertOne(id)
    }

}