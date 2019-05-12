package dev.olog.msc.data.repository

import android.content.Context
import android.provider.MediaStore
import dev.olog.msc.core.coroutines.merge
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.ChunkedData
import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.ArtistGateway
import dev.olog.msc.core.gateway.SongGateway
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.mapper.toArtist
import dev.olog.msc.data.repository.queries.ArtistQueries
import dev.olog.msc.data.repository.util.CommonQuery
import dev.olog.msc.data.repository.util.ContentObserver
import dev.olog.msc.shared.utils.clamp
import io.reactivex.Completable
import io.reactivex.Observable
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.flow.asFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

internal class ArtistRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val songGateway: SongGateway,
    appDatabase: AppDatabase,
    private val usedImageGateway: UsedImageGateway,
    private val prefsGateway: AppPreferencesGateway,
    private val contentObserver: ContentObserver

) : ArtistGateway {

    private val artistQueries = ArtistQueries(prefsGateway, false)

    private val lastPlayedDao = appDatabase.lastPlayedArtistDao()

    private suspend fun queryAll(): Flow<List<Artist>> {
        return flowOf()
//        return contentResolver.createQuery<List<Artist>>(
//            MEDIA_STORE_URI, arrayOf("count(*) as size"), null,
//            null, " size ASC LIMIT 1", true
//        ).mapToOne { listOf() }
//            .emitOnlyWithStoragePermission()
//            .adjust()
//            .distinctUntilChanged()
    }

    private suspend fun querySingle(selection: String, args: Array<String>): Flow<Artist> {
        return flow { }
//        return contentResolver.createQuery<List<Artist>>(
//            MEDIA_STORE_URI, arrayOf("count(*) as size"), selection,
//            args, " size ASC LIMIT 1", true
//        ).mapToOne { listOf() }
//            .emitOnlyWithStoragePermission()
//            .adjust()
//            .map { it.first() }
//            .distinctUntilChanged()
    }

    private fun getAllSize(): Int {
        return CommonQuery.sizeQuery(artistQueries.size(context, prefsGateway.getBlackList()))
    }

    override fun getChunk(): ChunkedData<Artist> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = artistQueries.all(context, prefsGateway.getBlackList(), chunkRequest)
                CommonQuery.query(cursor, { it.toArtist() }, { updateImages(it) })
            },
            allDataSize = getAllSize(),
            observeChanges = {
                contentObserver.createQuery(ArtistQueries.MEDIA_STORE_URI)
                    .merge(prefsGateway.observeAllArtistsSortOrder().drop(1)) // ignores emission on subscribe
            }
        )
    }

    private fun updateImages(list: List<Artist>): List<Artist> {
        val allForArtists = usedImageGateway.getAllForArtists()
        if (allForArtists.isEmpty()) {
            return list
        }
        return list.map { artist ->
            val image = allForArtists.firstOrNull { it.id == artist.id }?.image ?: artist.image
            artist.copy(image = image)
        }
    }

    override suspend fun getAll(): Flow<List<Artist>> {
        return queryAll()
    }

    override suspend fun getByParam(param: Long): Flow<Artist> {
        return querySingle("${MediaStore.Audio.Media.ARTIST_ID} = ?", arrayOf(param.toString()))
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(artistId: Long): Observable<List<Song>> = runBlocking {
        songGateway.getAll().asObservable().map {
            it.asSequence().filter { it.artistId == artistId }.toList()
        }.distinctUntilChanged()
    }

    override fun getLastPlayedChunk(): ChunkedData<Artist> {
        val maxAllowed = 10
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val lastPlayed = lastPlayedDao.getAll(maxAllowed)
                val existingLastCursor =
                    artistQueries.existingLastPlayed(context, prefsGateway.getBlackList(), lastPlayed.joinToString { "'${it.id}'" })
                val existingLastPlayed = CommonQuery.query(existingLastCursor, { it.toArtist() }, { updateImages(it) })
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

    override fun getRecentlyAddedChunk(): ChunkedData<Artist> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = artistQueries.recentlyAdded(context, prefsGateway.getBlackList(), chunkRequest)
                CommonQuery.query(cursor, { it.toArtist() }, { updateImages(it) })
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