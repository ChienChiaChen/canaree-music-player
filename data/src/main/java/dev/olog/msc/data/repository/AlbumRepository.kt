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
import dev.olog.msc.core.gateway.track.AlbumGateway
import dev.olog.msc.core.gateway.track.SongGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.mapper.toAlbum
import dev.olog.msc.data.mapper.toArtist
import dev.olog.msc.data.mapper.toSong
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

internal class AlbumRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val songGateway: SongGateway,
    appDatabase: AppDatabase,
    private val usedImageGateway: UsedImageGateway,
    private val contentObserver: ContentObserver,
    private val prefsGateway: AppPreferencesGateway,
    private val contentResolverFlow: ContentResolverFlow

) : AlbumGateway {

    companion object {
        internal fun updateImages(context: Context, list: List<Album>, usedImageGateway: UsedImageGateway): List<Album> {
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
    private val albumQueries = AlbumQueries(prefsGateway, false, contentResolver)

    private val lastPlayedDao = appDatabase.lastPlayedAlbumDao()

    override suspend fun getAll(): Flow<List<Album>> {
        return flowOf()
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(albumId: Long): Observable<List<Song>> = runBlocking {
        songGateway.getAll().asObservable().map { it.filter { it.albumId == albumId } }
    }

    private fun getAllSize(): Int {
        val cursor = albumQueries.countAll()
        return contentResolver.querySize(cursor)
    }

    override fun getChunk(): ChunkedData<Album> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = albumQueries.getAll(chunkRequest)
                contentResolver.queryAll(cursor, { it.toAlbum() }, { updateImages(context, it, usedImageGateway) })
            },
            allDataSize = getAllSize(),
            observeChanges = {
                contentObserver.createQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
                    .merge(prefsGateway.observeAllAlbumsSortOrder().drop(1)) // ignores emission on subscribe
            }
        )
    }

    override fun getByParam(param: Long): Album {
        return contentResolver.querySingle(albumQueries.getById(param), { it.toAlbum() }, {
            updateImages(context, listOf(it), usedImageGateway).first()
        })
    }

    override suspend fun observeByParam(param: Long): Flow<Album> {
        return contentResolverFlow.createQuery<Album>({ albumQueries.getById(param) }, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            .mapToList { it.toAlbum() }
            .map { updateImages(context, it, usedImageGateway).first() }
            .distinctUntilChanged()
    }

    override suspend fun observeArtistByAlbumId(albumId: Long): Flow<Artist> {
        return contentResolverFlow.createQuery<Artist>({ albumQueries.getArtistById(albumId) }, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            .mapToList { it.toArtist() }
            .map { ArtistRepository.updateImages(it, usedImageGateway).first() }
            .distinctUntilChanged()
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
                    albumQueries.getExistingLastPlayed(lastPlayed.joinToString { "'${it.id}'" })
                val existingLastPlayed = contentResolver.queryAll(existingLastCursor, { it.toAlbum() }, {
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

    override fun getRecentlyAddedChunk(): ChunkedData<Album> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = albumQueries.getRecentlyAddedAlbums(chunkRequest)
                contentResolver.queryAll(cursor, { it.toAlbum() }, { updateImages(context, it, usedImageGateway) })
            },
            allDataSize = contentResolver.queryCountRow(albumQueries.getRecentlyAddedAlbums(null)),
            observeChanges = { contentObserver.createQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) }
        )
    }

    override fun getSongListByParamChunk(param: Long): ChunkedData<Song> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = albumQueries.getSongList(param, chunkRequest)
                contentResolver.queryAll(cursor, { it.toSong() }, {
                    val result = SongRepository.adjustImages(context, it)
                    SongRepository.updateImages(result, usedImageGateway)
                })
            },
            allDataSize = contentResolver.querySize(albumQueries.countSongList(param)),
            observeChanges = { contentObserver.createQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) }
        )
    }

    override fun getSongListByParamDuration(param: Long): Int {
        return contentResolver.querySize(albumQueries.getSongListDuration(param))
    }

    private fun getSiblingsSize(mediaId: MediaId): Int {
        return contentResolver.queryCountRow(albumQueries.getSiblingsChunk(mediaId.categoryId, null))
    }

    override fun getSiblingsChunk(mediaId: MediaId): ChunkedData<Album> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = albumQueries.getSiblingsChunk(mediaId.categoryId, chunkRequest)
                contentResolver.queryAll(cursor, { it.toAlbum() }, { updateImages(context, it, usedImageGateway) })
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