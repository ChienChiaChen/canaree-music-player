package dev.olog.msc.data.repository

import android.content.Context
import android.provider.MediaStore
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.PageRequest
import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.entity.track.Genre
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.track.GenreGateway
import dev.olog.msc.core.gateway.track.SongGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.GenreMostPlayedEntity
import dev.olog.msc.data.entity.custom.ItemRequestImpl
import dev.olog.msc.data.entity.custom.PageRequestDao
import dev.olog.msc.data.entity.custom.PageRequestImpl
import dev.olog.msc.data.mapper.toArtist
import dev.olog.msc.data.mapper.toGenre
import dev.olog.msc.data.mapper.toSong
import dev.olog.msc.data.repository.queries.GenreQueries
import dev.olog.msc.data.repository.util.ContentObserverFlow
import dev.olog.msc.data.repository.util.queryCountRow
import dev.olog.msc.data.repository.util.querySize
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

internal class GenreRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefsGateway: AppPreferencesGateway,
    appDatabase: AppDatabase,
    private val usedImageGateway: UsedImageGateway,
    private val contentObserverFlow: ContentObserverFlow,
    private val songGateway: SongGateway

) : GenreGateway {

    private val contentResolver = context.contentResolver
    private val queries = GenreQueries(prefsGateway, contentResolver)

    private val mostPlayedDao = appDatabase.genreMostPlayedDao()

    override fun getAll(): PageRequest<Genre> {
        return PageRequestImpl(
            cursorFactory = { queries.getAll(it) },
            cursorMapper = { it.toGenre(context) },
            listMapper = { genreList ->
                genreList.map { genre ->
                    // get the size for every genre
                    val sizeQueryCursor = queries.countGenreSize(genre.id)
                    val sizeQuery = contentResolver.queryCountRow(sizeQueryCursor)
                    genre.copy(size = sizeQuery)
                }
            },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI
        )
    }

    override fun getByParam(param: Long): ItemRequestImpl<Genre> {
        return ItemRequestImpl(
            cursorFactory = { queries.getById(param) },
            cursorMapper = { it.toGenre(context) },
            itemMapper = { genre ->
                // get the size for every genre
                val sizeQueryCursor = queries.countGenreSize(genre.id)
                val sizeQuery = contentResolver.queryCountRow(sizeQueryCursor)
                genre.copy(size = sizeQuery)
            },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI
        )
    }

    override fun getSongListByParam(param: Long): PageRequest<Song> {
        return PageRequestImpl(
            cursorFactory = { queries.getSongList(param, it) },
            cursorMapper = { it.toSong() },
            listMapper = {
                val result = SongRepository.adjustImages(context, it)
                SongRepository.updateImages(result, usedImageGateway)
            },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = MediaStore.Audio.Genres.Members.getContentUri("external", param)
        )
    }

    override fun getSongListByParamDuration(param: Long): Int {
        return contentResolver.querySize(queries.getSongListDuration(param))
    }

    override fun getRecentlyAddedSongs(mediaId: MediaId): PageRequest<Song> {
        return PageRequestImpl(
            cursorFactory = { queries.getRecentlyAddedSongs(mediaId.categoryId, it) },
            cursorMapper = { it.toSong() },
            listMapper = {
                val result = SongRepository.adjustImages(context, it)
                SongRepository.updateImages(result, usedImageGateway)
            },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = MediaStore.Audio.Genres.Members.getContentUri("external", mediaId.categoryId)
        )
    }

    override fun canShowRecentlyAddedSongs(mediaId: MediaId): Boolean {
        return getRecentlyAddedSongs(mediaId).getCount() > 0 && prefsGateway.getVisibleTabs()[1]
    }

    override fun getSiblings(mediaId: MediaId): PageRequest<Genre> {
        return PageRequestImpl(
            cursorFactory = { queries.getSiblings(mediaId.categoryId, it) },
            cursorMapper = { it.toGenre(context) },
            listMapper = { genreList ->
                genreList.map { genre ->
                    // get the size for every playlist
                    val sizeQueryCursor = queries.countGenreSize(genre.id)
                    val sizeQuery = contentResolver.queryCountRow(sizeQueryCursor)
                    genre.copy(size = sizeQuery)
                }
            },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI
        )
    }

    override fun canShowSiblings(mediaId: MediaId): Boolean {
        return getSiblings(mediaId).getCount() > 0
    }

    override fun getRelatedArtists(mediaId: MediaId): PageRequest<Artist> {
        return PageRequestImpl(
            cursorFactory = { queries.getRelatedArtists(mediaId.categoryId, it) },
            cursorMapper = { it.toArtist() },
            listMapper = { ArtistRepository.updateImages(it, usedImageGateway) },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = MediaStore.Audio.Genres.Members.getContentUri("external", mediaId.categoryId)
        )
    }

    override fun canShowRelatedArtists(mediaId: MediaId): Boolean {
        return getRelatedArtists(mediaId).getCount() > 0 && prefsGateway.getVisibleTabs()[2]
    }


    private fun getMostPlayedSize(mediaId: MediaId): Int {
        return mostPlayedDao.count(mediaId.categoryId)
    }

    override fun canShowMostPlayed(mediaId: MediaId): Boolean {
        return getMostPlayedSize(mediaId) > 0 && prefsGateway.getVisibleTabs()[0]
    }

    override fun getMostPlayed(mediaId: MediaId): PageRequest<Song> {
        val maxAllowed = 10
        return PageRequestDao(
            cursorFactory = {
                val mostPlayed = mostPlayedDao.query(mediaId.categoryId, maxAllowed)
                queries.getExisting(mostPlayed.joinToString { "'${it.songId}'" })
            },
            cursorMapper = { it.toSong() },
            listMapper = { list, _ ->
                val result = SongRepository.adjustImages(context, list)
                val existing = SongRepository.updateImages(result, usedImageGateway)
                val mostPlayed = mostPlayedDao.query(mediaId.categoryId, maxAllowed)
                mostPlayed.asSequence()
                    .mapNotNull { mostPlayed -> existing.firstOrNull { it.id == mostPlayed.songId } }
                    .take(maxAllowed)
                    .toList()
            },
            contentResolver = contentResolver,
            changeNotification = { mostPlayedDao.observe(mediaId.categoryId, maxAllowed).asFlow() }
        )
    }

    override suspend fun insertMostPlayed(mediaId: MediaId) {
        val songId = mediaId.leaf!!
        val genreId = mediaId.categoryValue.toLong()
        songGateway.getByParam(songId).getItem()?.let { song ->
            mostPlayedDao.insertOne(GenreMostPlayedEntity(0, song.id, genreId))
        }
    }

}