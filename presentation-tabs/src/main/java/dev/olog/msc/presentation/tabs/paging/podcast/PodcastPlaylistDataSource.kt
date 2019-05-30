package dev.olog.msc.presentation.tabs.paging.podcast

import android.content.Context
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.msc.presentation.base.list.model.DisplayableItem
import dev.olog.msc.presentation.base.list.paging.BaseDataSource
import dev.olog.msc.presentation.base.list.paging.BaseDataSourceFactory
import dev.olog.msc.presentation.tabs.TabFragmentHeaders
import dev.olog.msc.presentation.tabs.mapper.toAutoPlaylist
import dev.olog.msc.presentation.tabs.mapper.toTabDisplayableItem
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

internal class PodcastPlaylistDataSource @Inject constructor(
    @ApplicationContext context: Context,
    private val gateway: PodcastPlaylistGateway,
    private val displayableHeaders: TabFragmentHeaders
) : BaseDataSource<DisplayableItem>() {

    private val resources = context.resources

    private val chunked = gateway.getAll()

    override fun onAttach() {
        launch {
            chunked.observeNotification()
                .take(1)
                .collect {
                    invalidate()
                }
        }
    }

    override suspend fun getMainDataSize(): Int {
        return chunked.getCount(Filter.NO_FILTER)
    }

    override suspend fun getHeaders(mainListSize: Int): List<DisplayableItem> {
        val headers = mutableListOf(displayableHeaders.autoPlaylistHeader)
        headers.addAll(gateway.getAllAutoPlaylists().map { it.toAutoPlaylist() })

        if (mainListSize > 0) {
            headers.add(displayableHeaders.allPlaylistHeader)
        }
        return headers
    }

    override suspend fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun loadInternal(request: Request): List<DisplayableItem> {
        return chunked.getPage(request)
            .map { it.toTabDisplayableItem(resources) }
    }

}

internal class PodcastPlaylistDataSourceFactory @Inject constructor(
    dataSourceProvider: Provider<PodcastPlaylistDataSource>
) : BaseDataSourceFactory<DisplayableItem, PodcastPlaylistDataSource>(dataSourceProvider)