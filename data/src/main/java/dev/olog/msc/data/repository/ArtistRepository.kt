package dev.olog.msc.data.repository

import android.content.Context
import android.provider.MediaStore.Audio.Media
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.ItemRequest
import dev.olog.msc.core.entity.PageRequest
import dev.olog.msc.core.entity.track.Album
import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.track.ArtistGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.custom.ItemRequestImpl
import dev.olog.msc.data.entity.custom.PageRequestDao
import dev.olog.msc.data.entity.custom.PageRequestImpl
import dev.olog.msc.data.mapper.toAlbum
import dev.olog.msc.data.mapper.toArtist
import dev.olog.msc.data.mapper.toSong
import dev.olog.msc.data.repository.queries.ArtistQueries
import dev.olog.msc.data.repository.util.ContentObserverFlow
import dev.olog.msc.data.repository.util.querySize
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

internal class ArtistRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    appDatabase: AppDatabase,
    private val usedImageGateway: UsedImageGateway,
    private val prefsGateway: AppPreferencesGateway,
    private val contentObserverFlow: ContentObserverFlow

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
    private val queries = ArtistQueries(prefsGateway, false, contentResolver)

    private val lastPlayedDao = appDatabase.lastPlayedArtistDao()

    override fun getAll(): PageRequest<Artist> {
        return PageRequestImpl(
            cursorFactory = { queries.getAll(it) },
            cursorMapper = { it.toArtist() },
            listMapper = { updateImages(it, usedImageGateway) },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun getByParam(param: Long): ItemRequest<Artist> {
        return ItemRequestImpl(
            cursorFactory = { queries.getById(param) },
            cursorMapper = { it.toArtist() },
            itemMapper = { updateImages(listOf(it), usedImageGateway).first() },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun getLastPlayed(): PageRequest<Artist> {
        val maxAllowed = 10
        return PageRequestDao(
            cursorFactory = {
                val lastPlayed = lastPlayedDao.getAll(maxAllowed)
                queries.getExistingLastPlayed(lastPlayed.joinToString { "'${it.id}'" })
            },
            cursorMapper = { it.toArtist() },
            listMapper = { list, _ ->
                val lastPlayed = lastPlayedDao.getAll(maxAllowed)
                val existingLastPlayed = updateImages(list, usedImageGateway)
                lastPlayed.asSequence()
                    .mapNotNull { last -> existingLastPlayed.firstOrNull { it.id == last.id } }
                    .take(maxAllowed)
                    .toList()
            },
            contentResolver = contentResolver,
            changeNotification = { lastPlayedDao.observeAll(1).asFlow() }
        )
    }

    override fun canShowLastPlayed(): Boolean {
        return prefsGateway.canShowLibraryRecentPlayedVisibility() &&
                getAll().getCount() >= 5 &&
                lastPlayedDao.getCount() > 0
    }

    override fun getRecentlyAdded(): PageRequest<Artist> {
        return PageRequestImpl(
            cursorFactory = { queries.getRecentlyAdded(it) },
            cursorMapper = { it.toArtist() },
            listMapper = { updateImages(it, usedImageGateway) },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = Media.EXTERNAL_CONTENT_URI
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
            mediaStoreUri = Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun getSongListByParamDuration(param: Long): Int {
        return contentResolver.querySize(queries.getSongListDuration(param))
    }

    override fun getSiblings(mediaId: MediaId): PageRequest<Album> {
        return PageRequestImpl(
            cursorFactory = { queries.getSiblings(mediaId.categoryId, it) },
            cursorMapper = { it.toAlbum() },
            listMapper = { AlbumRepository.updateImages(context, it, usedImageGateway) },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun canShowSiblings(mediaId: MediaId): Boolean {
        return getSiblings(mediaId).getCount() > 0
    }

    override fun canShowRecentlyAdded(): Boolean {
        return prefsGateway.canShowLibraryNewVisibility() &&
                getRecentlyAdded().getCount() > 0
    }

    override suspend fun addLastPlayed(id: Long) {
        lastPlayedDao.insertOne(id)
    }

}