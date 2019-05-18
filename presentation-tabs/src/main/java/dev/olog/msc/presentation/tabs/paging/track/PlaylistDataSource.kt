package dev.olog.msc.presentation.tabs.paging.track

import android.content.res.Resources
import androidx.lifecycle.Lifecycle
import androidx.paging.DataSource
import dev.olog.msc.core.dagger.qualifier.ActivityLifecycle
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.gateway.track.PlaylistGateway
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

internal class PlaylistDataSource @Inject constructor(
    @ActivityLifecycle lifecycle: Lifecycle,
    private val resources: Resources,
    private val gateway: PlaylistGateway,
    private val displayableHeaders: TabFragmentHeaders
) : BaseDataSource<DisplayableItem>() {

    private val chunked = gateway.getAll()

    init {
        launch {
            withContext(Dispatchers.Main) { lifecycle.addObserver(this@PlaylistDataSource) }
            chunked.observeNotification()
                .take(1)
                .collect {
                    invalidate()
                }
        }
    }

    override fun getMainDataSize(): Int {
        return chunked.getCount(Filter.NO_FILTER)
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

    override fun loadInternal(page: Request): List<DisplayableItem> {
        return chunked.getPage(page)
            .map { it.toTabDisplayableItem(resources) }
    }

}

internal class PlaylistDataSourceFactory @Inject constructor(
    private val dataSource: Provider<PlaylistDataSource>
) : DataSource.Factory<Int, DisplayableItem>() {

    override fun create(): DataSource<Int, DisplayableItem> {
        return dataSource.get()
    }
}