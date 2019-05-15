package dev.olog.msc.data.repository

import android.content.Context
import android.provider.MediaStore
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.merge
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.ChunkedData
import dev.olog.msc.core.entity.track.Album
import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.track.ArtistGateway
import dev.olog.msc.core.gateway.track.SongGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.mapper.toAlbum
import dev.olog.msc.data.mapper.toArtist
import dev.olog.msc.data.mapper.toSong
import dev.olog.msc.data.repository.queries.ArtistQueries
import dev.olog.msc.data.repository.util.*
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
    private val contentObserver: ContentObserver,
    private val contentResolverFlow: ContentResolverFlow

) : ArtistGateway {

    companion object {
        internal fun updateImages(list: List<Artist>, usedImageGateway: UsedImageGateway): List<Artist> {
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
    private val artistQueries = ArtistQueries(prefsGateway, false, contentResolver)

    private val lastPlayedDao = appDatabase.lastPlayedArtistDao()

    override suspend fun getAll(): Flow<List<Artist>> {
        return flowOf()
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(artistId: Long): Observable<List<Song>> = runBlocking {
        songGateway.getAll().asObservable().map {
            it.asSequence().filter { it.artistId == artistId }.toList()
        }.distinctUntilChanged()
    }

    private fun getAllSize(): Int {
        val size = contentResolver.querySize(artistQueries.countAll())
        return size
    }

    override fun getChunk(): ChunkedData<Artist> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = artistQueries.getAll(chunkRequest)
                contentResolver.queryAll(cursor, { it.toArtist() }, { updateImages(it, usedImageGateway) })
            },
            allDataSize = getAllSize(),
            observeChanges = {
                contentObserver.createQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
                    .merge(prefsGateway.observeAllArtistsSortOrder().drop(1)) // ignores emission on subscribe
            }
        )
    }

    override fun getByParam(param: Long): Artist {
        val cursor = artistQueries.getById(param)
        return contentResolver.querySingle(cursor, { it.toArtist() }, {
            updateImages(listOf(it), usedImageGateway).first()
        })
    }

    override suspend fun observeByParam(param: Long): Flow<Artist> {
        return contentResolverFlow.createQuery<Artist>({ artistQueries.getById(param) }, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            .mapToList { it.toArtist() }
            .map { updateImages(it, usedImageGateway).first() }
            .distinctUntilChanged()
    }

    override fun getLastPlayedChunk(): ChunkedData<Artist> {
        val maxAllowed = 10
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val lastPlayed = lastPlayedDao.getAll(maxAllowed)
                val existingLastCursor =
                    artistQueries.getExistingLastPlayed(lastPlayed.joinToString { "'${it.id}'" })
                val existingLastPlayed = contentResolver.queryAll(existingLastCursor, { it.toArtist() }, {
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

    override fun getRecentlyAddedChunk(): ChunkedData<Artist> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = artistQueries.getRecentlyAdded(chunkRequest)
                contentResolver.queryAll(cursor, { it.toArtist() }, { updateImages(it, usedImageGateway) })
            },
            allDataSize = contentResolver.queryCountRow(artistQueries.getRecentlyAdded(null)),
            observeChanges = { contentObserver.createQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) }
        )
    }

    override fun getSongListByParamChunk(param: Long): ChunkedData<Song> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = artistQueries.getSongList(param, chunkRequest)
                contentResolver.queryAll(cursor, { it.toSong() }, {
                    val result = SongRepository.adjustImages(context, it)
                    SongRepository.updateImages(result, usedImageGateway)
                })
            },
            allDataSize = contentResolver.querySize(artistQueries.countSongList(param)),
            observeChanges = { contentObserver.createQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) }
        )
    }

    override fun getSongListByParamDuration(param: Long): Int {
        return contentResolver.querySize(artistQueries.getSongListDuration(param))
    }

    private fun getSiblingsSize(mediaId: MediaId): Int {
        return contentResolver.queryCountRow(artistQueries.getSiblingsChunk(mediaId.categoryId, null))
    }

    override fun getSiblingsChunk(mediaId: MediaId): ChunkedData<Album> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = artistQueries.getSiblingsChunk(mediaId.categoryId, chunkRequest)
                contentResolver.queryAll(cursor, { it.toAlbum() }, { AlbumRepository.updateImages(context, it, usedImageGateway) })
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