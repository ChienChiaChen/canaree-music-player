package dev.olog.msc.data.repository

import android.content.Context
import android.provider.MediaStore
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.PageRequest
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.SearchGateway
import dev.olog.msc.core.gateway.SearchGateway.SearchRequest
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.data.entity.custom.PageRequestImpl
import dev.olog.msc.data.mapper.toPodcast
import dev.olog.msc.data.mapper.toSong
import dev.olog.msc.data.repository.podcast.PodcastRepository
import dev.olog.msc.data.repository.queries.SearchQueries
import dev.olog.msc.data.repository.queries.SearchQueries.SearchType
import dev.olog.msc.data.repository.util.ContentObserverFlow
import dev.olog.msc.data.repository.util.queryAll
import dev.olog.msc.shared.utils.assertBackgroundThread
import javax.inject.Inject

internal class SearchRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val contentObserverFlow: ContentObserverFlow,
    prefsGateway: AppPreferencesGateway,
    private val usedImageGateway: UsedImageGateway
) : SearchGateway {

    private val contentResolver = context.contentResolver
    private val searchQueries = SearchQueries(prefsGateway, contentResolver)

    override fun searchSongsAndPocastsBy(request: SearchRequest): PageRequest<Song> {
        assertBackgroundThread()
        return PageRequestImpl(
            cursorFactory = { searchQueries.searchTrack(it, SearchType.ALL, request) },
            cursorMapper = { it.toSong() },
            listMapper = {
                val result = SongRepository.adjustImages(context, it)
                SongRepository.updateImages(result, usedImageGateway)
            },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun searchSongOnlyBy(request: SearchRequest): PageRequest<Song> {
        assertBackgroundThread()
        return PageRequestImpl(
            cursorFactory = { searchQueries.searchTrack(it, SearchType.SONGS, request) },
            cursorMapper = { it.toSong() },
            listMapper = {
                val result = SongRepository.adjustImages(context, it)
                SongRepository.updateImages(result, usedImageGateway)
            },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun searchPodcastOnlyBy(request: SearchRequest): PageRequest<Podcast> {
        assertBackgroundThread()
        return PageRequestImpl(
            cursorFactory = { searchQueries.searchTrack(it, SearchType.PODCAST, request) },
            cursorMapper = { it.toPodcast() },
            listMapper = {
                PodcastRepository.adjustImages(context, it)
            },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun searchSongsInGenre(genre: String): List<Song>? {
        assertBackgroundThread()
        val cursor = searchQueries.searchTracksInGenre(genre) ?: return null
        return contentResolver.queryAll(cursor, { it.toSong() }, {
            val result = SongRepository.adjustImages(context, it)
            SongRepository.updateImages(result, usedImageGateway)
        })
    }
}