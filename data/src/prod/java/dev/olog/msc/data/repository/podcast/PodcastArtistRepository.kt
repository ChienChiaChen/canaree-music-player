package dev.olog.msc.data.repository.podcast

import android.content.Context
import android.provider.MediaStore
import dev.olog.msc.core.coroutines.debounceFirst
import dev.olog.msc.core.coroutines.withLatest
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.ChunkedData
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.podcast.PodcastArtist
import dev.olog.msc.core.gateway.PodcastArtistGateway
import dev.olog.msc.core.gateway.PodcastGateway
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.mapper.toArtist
import dev.olog.msc.data.mapper.toPodcastArtist
import dev.olog.msc.data.repository.queries.ArtistQueries
import dev.olog.msc.data.repository.util.CommonQuery
import dev.olog.msc.data.repository.util.ContentObserver
import dev.olog.msc.data.repository.util.ContentResolverFlow
import dev.olog.msc.shared.TrackUtils
import dev.olog.msc.shared.collator
import dev.olog.msc.shared.extensions.safeCompare
import dev.olog.msc.shared.utils.clamp
import io.reactivex.Completable
import io.reactivex.Observable
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.flow.asFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

internal class PodcastArtistRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    appDatabase: AppDatabase,
    private val podcastGateway: PodcastGateway,
    private val usedImageGateway: UsedImageGateway,
    private val prefsGateway: AppPreferencesGateway,
    private val contentObserver: ContentObserver

) : PodcastArtistGateway {

    private val artistQueries = ArtistQueries(prefsGateway, true)

    private val lastPlayedDao = appDatabase.lastPlayedPodcastArtistDao()

    private suspend fun queryAll(): Flow<List<PodcastArtist>> {
        return flowOf()
    }

    private suspend fun querySingle(selection: String, args: Array<String>): Flow<PodcastArtist> {
        return flow { }
    }

    private fun getAllSize(): Int {
        return CommonQuery.sizeQuery(artistQueries.size(context, prefsGateway.getBlackList()))
    }

    override fun getChunk(): ChunkedData<PodcastArtist> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = artistQueries.all(context, prefsGateway.getBlackList(), chunkRequest)
                CommonQuery.query(cursor, { it.toPodcastArtist() }, { updateImages(it) })
            },
            allDataSize = getAllSize(),
            observeChanges = { contentObserver.createQuery(ArtistQueries.MEDIA_STORE_URI) }
        )
    }

    private fun updateImages(list: List<PodcastArtist>): List<PodcastArtist> {
        val allForArtists = usedImageGateway.getAllForArtists()
        if (allForArtists.isEmpty()) {
            return list
        }
        return list.map { artist ->
            val image = allForArtists.firstOrNull { it.id == artist.id }?.image ?: artist.image
            artist.copy(image = image)
        }
    }

    override suspend fun getAll(): Flow<List<PodcastArtist>> {
        return queryAll()
    }

    override suspend fun getByParam(param: Long): Flow<PodcastArtist> {
        return querySingle("${MediaStore.Audio.Media.ARTIST_ID} = ?", arrayOf(param.toString()))
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observePodcastListByParam(artistId: Long): Observable<List<Podcast>> = runBlocking {
        podcastGateway.getAll().asObservable()
            .map { it.filter { it.artistId == artistId } }
    }

    override fun getLastPlayedChunk(): ChunkedData<PodcastArtist> {
        val maxAllowed = 10
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val lastPlayed = lastPlayedDao.getAll(maxAllowed)
                val existingLastCursor =
                    artistQueries.existingLastPlayed(context, prefsGateway.getBlackList(), lastPlayed.joinToString { "'${it.id}'" })
                val existingLastPlayed = CommonQuery.query(existingLastCursor, { it.toPodcastArtist() }, { updateImages(it) })
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
                val cursor = artistQueries.recentlyAdded(context, prefsGateway.getBlackList(), chunkRequest)
                CommonQuery.query(cursor, { it.toPodcastArtist() }, { updateImages(it) })
            },
            allDataSize = CommonQuery.sizeQuery(
                artistQueries.recentlyAdded(
                    context,
                    prefsGateway.getBlackList(),
                    null
                )
            ),
            observeChanges = { contentObserver.createQuery(ArtistQueries.MEDIA_STORE_URI) }
        )
    }

    override fun canShowRecentlyAdded(): Boolean {
        val cursor = artistQueries.recentlyAdded(context, prefsGateway.getBlackList(), null)
        return prefsGateway.canShowLibraryNewVisibility() &&
                CommonQuery.sizeQuery(cursor) > 0
    }

    override fun addLastPlayed(id: Long): Completable {
        return lastPlayedDao.insertOne(id)
    }

}