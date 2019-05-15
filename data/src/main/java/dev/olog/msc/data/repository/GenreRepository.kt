package dev.olog.msc.data.repository

import android.content.Context
import android.provider.MediaStore
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.ChunkRequest
import dev.olog.msc.core.entity.ChunkedData
import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.entity.track.Genre
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.track.GenreGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.mapper.toArtist
import dev.olog.msc.data.mapper.toGenre
import dev.olog.msc.data.mapper.toSong
import dev.olog.msc.data.repository.queries.GenreQueries
import dev.olog.msc.data.repository.util.*
import dev.olog.msc.shared.utils.clamp
import io.reactivex.Completable
import io.reactivex.Observable
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

internal class GenreRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefsGateway: AppPreferencesGateway,
    appDatabase: AppDatabase,
    private val contentObserver: ContentObserver,
    private val usedImageGateway: UsedImageGateway,
    private val contentResolverFlow: ContentResolverFlow

) : GenreGateway {

    private val contentResolver = context.contentResolver
    private val genreQueries = GenreQueries(prefsGateway, contentResolver)

    private val mostPlayedDao = appDatabase.genreMostPlayedDao()

    override suspend fun getAll(): Flow<List<Genre>> {
        return flowOf()
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(genreId: Long): Observable<List<Song>> {
        return Observable.just(listOf())
    }


    override fun getChunk(): ChunkedData<Genre> {
        return ChunkedData(
            chunkOf = { chunkRequest -> makeChunkOf(chunkRequest) },
            allDataSize = contentResolver.querySize(genreQueries.countAll()),
            observeChanges = { contentObserver.createQuery(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI) }
        )
    }

    private fun makeChunkOf(chunkRequest: ChunkRequest): List<Genre> {
        return contentResolver.queryAll(
            cursor = genreQueries.getAll(chunkRequest),
            mapper = { it.toGenre(context, 0) },
            afterQuery = { genreList ->
                genreList.map { genre ->
                    // get the size for every playlist
                    val sizeQueryCursor = genreQueries.countGenreSize(genre.id)
                    val sizeQuery = contentResolver.queryCountRow(sizeQueryCursor)
                    genre.copy(size = sizeQuery)
                }
            })
    }

    override fun getByParam(param: Long): Genre {
        val cursor = genreQueries.getById(param)
        return contentResolver.queryAll(cursor, { it.toGenre(context, 0) }, { genreList ->
            genreList.map { genre ->
                // get the size for every playlist
                val sizeQueryCursor = genreQueries.countGenreSize(genre.id)
                val sizeQuery = contentResolver.queryCountRow(sizeQueryCursor)
                genre.copy(size = sizeQuery)
            }
        }).first()
    }

    override suspend fun observeByParam(param: Long): Flow<Genre> {
        return contentResolverFlow.createQuery<Genre>({ genreQueries.getById(param) }, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            .mapToOne { it.toGenre(context, 0) }
            .map { genre ->
                val sizeQueryCursor = genreQueries.countGenreSize(genre.id)
                val sizeQuery = contentResolver.queryCountRow(sizeQueryCursor)
                genre.copy(size = sizeQuery)
            }
            .distinctUntilChanged()
    }

    override fun getSongListByParamChunk(param: Long): ChunkedData<Song> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = genreQueries.getSongList(param, chunkRequest)
                contentResolver.queryAll(cursor, { it.toSong() }, {
                    val result = SongRepository.adjustImages(context, it)
                    SongRepository.updateImages(result, usedImageGateway)
                })
            },
            allDataSize = contentResolver.queryCountRow(genreQueries.getSongList(param, null)),
            observeChanges = { contentObserver.createQuery(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI) }
        )
    }

    override fun getSongListByParamDuration(param: Long): Int {
        return contentResolver.querySize(genreQueries.getSongListDuration(param))
    }

    override fun getRecentlyAddedSongsSize(mediaId: MediaId): Int {
        val cursor = genreQueries.getRecentlyAddedSongs(mediaId.categoryId, null)
        return contentResolver.queryCountRow(cursor)
    }

    override fun getRecentlyAddedSongsChunk(mediaId: MediaId): ChunkedData<Song> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = genreQueries.getRecentlyAddedSongs(mediaId.categoryId, chunkRequest)
                contentResolver.queryAll(cursor, { it.toSong() }, {
                    val result = SongRepository.adjustImages(context, it)
                    SongRepository.updateImages(result, usedImageGateway)
                })
            },
            allDataSize = getRecentlyAddedSongsSize(mediaId),
            observeChanges = { contentObserver.createQuery(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI) }
        )
    }

    override fun canShowRecentlyAddedSongs(mediaId: MediaId): Boolean {
        return getRecentlyAddedSongsSize(mediaId) > 0 && prefsGateway.getVisibleTabs()[1]
    }

    private fun getSiblingsSize(mediaId: MediaId): Int {
        val cursor = genreQueries.getSiblingsChunk(mediaId.categoryId, null)
        return contentResolver.queryCountRow(cursor)
    }

    override fun getSiblingsChunk(mediaId: MediaId): ChunkedData<Genre> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = genreQueries.getSiblingsChunk(mediaId.categoryId, chunkRequest)
                contentResolver.queryAll(cursor, { it.toGenre(context, 0) }, { genreList ->
                    genreList.map { genre ->
                        // get the size for every playlist
                        val sizeQueryCursor = genreQueries.countGenreSize(genre.id)
                        val sizeQuery = contentResolver.queryCountRow(sizeQueryCursor)
                        genre.copy(size = sizeQuery)
                    }
                })
            },
            allDataSize = getSiblingsSize(mediaId),
            observeChanges = { contentObserver.createQuery(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI) }
        )
    }

    override fun canShowSiblings(mediaId: MediaId): Boolean {
        return getSiblingsSize(mediaId) > 0
    }

    override fun getRelatedArtistsSize(mediaId: MediaId): Int {
        val cursor = genreQueries.getRelatedArtists(mediaId.categoryId, null)
        return contentResolver.queryCountRow(cursor)
    }

    override fun getRelatedArtistsChunk(mediaId: MediaId): ChunkedData<Artist> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = genreQueries.getRelatedArtists(mediaId.categoryId, chunkRequest)
                contentResolver.queryAll(
                    cursor,
                    { it.toArtist() },
                    { ArtistRepository.updateImages(it, usedImageGateway) })
            },
            allDataSize = getRelatedArtistsSize(mediaId),
            observeChanges = { contentObserver.createQuery(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI) }
        )
    }

    override fun canShowRelatedArtists(mediaId: MediaId): Boolean {
        return getRecentlyAddedSongsSize(mediaId) > 0 && prefsGateway.getVisibleTabs()[2]
    }


    private fun getMostPlayedSize(mediaId: MediaId): Int {
        return mostPlayedDao.count(mediaId.categoryId)
    }

    override fun canShowMostPlayed(mediaId: MediaId): Boolean {
        return getMostPlayedSize(mediaId) > 0 && prefsGateway.getVisibleTabs()[0]
    }

    override fun getMostPlayedChunk(mediaId: MediaId): ChunkedData<Song> {
        val maxAllowed = 10
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val mostPlayed = mostPlayedDao.query(mediaId.categoryId, maxAllowed)
                val cursor = genreQueries.getExisting(mostPlayed.joinToString { "'${it.songId}'" })
                val existings = contentResolver.queryAll(cursor, { it.toSong() }, {
                    val result = SongRepository.adjustImages(context, it)
                    SongRepository.updateImages(result, usedImageGateway)
                })
                mostPlayed.asSequence()
                    .mapNotNull { mostPlayed -> existings.firstOrNull { it.id == mostPlayed.songId }  }
                    .take(maxAllowed)
                    .toList()
            },
            allDataSize = clamp(mostPlayedDao.count(mediaId.categoryId), 0, maxAllowed),
            observeChanges = {
                mostPlayedDao.observe(mediaId.categoryId, maxAllowed)
                    .asFlow()
                    .drop(1)
                    .map { Unit }
            }
        )
    }

    override fun insertMostPlayed(mediaId: MediaId): Completable {
        return Completable.complete()
//        val songId = mediaId.leaf!!
//        val genreId = mediaId.categoryValue.toLong()
//        songGateway.getByParam(songId).asObservable()
//            .firstOrError()
//            .flatMapCompletable { song ->
//                CompletableSource { mostPlayedDao.insertOne(GenreMostPlayedEntity(0, song.id, genreId)) }
//            }
    }

}