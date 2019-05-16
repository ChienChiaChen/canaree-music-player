package dev.olog.msc.presentation.tabs.paging.podcast

import android.content.res.Resources
import androidx.lifecycle.Lifecycle
import androidx.paging.DataSource
import dev.olog.msc.core.dagger.qualifier.ActivityLifecycle
import dev.olog.msc.core.entity.Page
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.base.paging.BaseDataSource
import dev.olog.msc.presentation.tabs.TabFragmentHeaders
import dev.olog.msc.presentation.tabs.mapper.toAutoPlaylist
import dev.olog.msc.presentation.tabs.mapper.toTabDisplayableItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider

internal class PodcastPlaylistDataSource @Inject constructor(
    @ActivityLifecycle lifecycle: Lifecycle,
    private val resources: Resources,
    private val gateway: PodcastPlaylistGateway,
    private val displayableHeaders: TabFragmentHeaders
) : BaseDataSource<DisplayableItem>() {

    private val chunked = gateway.getAll()

    init {
        launch {
            withContext(Dispatchers.Main) { lifecycle.addObserver(this@PodcastPlaylistDataSource) }
            chunked.observeNotification()
                .take(1)
                .collect {
                    invalidate()
                }
        }
    }

    override fun getMainDataSize(): Int {
        return chunked.getCount()
    }

    override fun getHeaders(mainListSize: Int): List<DisplayableItem> {
        val headers = mutableListOf(displayableHeaders.autoPlaylistHeader)
        headers.addAll(gateway.getAllAutoPlaylists().map { it.toAutoPlaylist() })

        if (mainListSize > 0) {
            headers.add(displayableHeaders.allPlaylistHeader)
        }
        return headers
    }

    override fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun loadInternal(page: Page): List<DisplayableItem> {
        return chunked.getPage(page)
            .map { it.toTabDisplayableItem(resources) }
    }

}

internal class PodcastPlaylistDataSourceFactory @Inject constructor(
    private val dataSource: Provider<PodcastPlaylistDataSource>
) : DataSource.Factory<Int, DisplayableItem>() {

    override fun create(): DataSource<Int, DisplayableItem> {
        return dataSource.get()
    }
}