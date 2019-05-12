package dev.olog.msc.presentation.tabs.paging.podcast

import android.content.res.Resources
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.paging.DataSource
import androidx.paging.PositionalDataSource
import dev.olog.msc.core.coroutines.CustomScope
import dev.olog.msc.core.dagger.qualifier.ActivityLifecycle
import dev.olog.msc.core.entity.ChunkRequest
import dev.olog.msc.core.gateway.PodcastArtistGateway
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.tabs.TabFragmentHeaders
import dev.olog.msc.presentation.tabs.mapper.toTabDisplayableItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

internal class PodcastArtistDataSource @Inject constructor(
    @ActivityLifecycle lifecycle: Lifecycle,
    private val resources: Resources,
    private val gateway: PodcastArtistGateway,
    private val headers: TabFragmentHeaders
) : PositionalDataSource<DisplayableItem>(), DefaultLifecycleObserver, CoroutineScope by CustomScope() {

    private val chunked = gateway.getChunk()

    init {
        launch(Dispatchers.Main) { lifecycle.addObserver(this@PodcastArtistDataSource) }
        launch {
            chunked.observeChanges()
                .collect {
                    cancel()
                    invalidate()
                }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        cancel()
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<DisplayableItem>) {
        val result = mutableListOf<DisplayableItem>()
        if (gateway.canShowRecentlyAdded()) {
            result.addAll(headers.recentlyAddedArtistsHeaders)
        }
        if (gateway.canShowLastPlayed()) {
            result.addAll(headers.lastPlayedArtistHeaders)
        }
        if (result.isNotEmpty()) {
            result.addAll(headers.allArtistsHeader)
        }

        val count = chunked.allDataSize + result.size
        val initialChunk = loadChunk(params.requestedStartPosition, params.requestedLoadSize - result.size)
        result.addAll(initialChunk)
        callback.onResult(result, 0, count)
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<DisplayableItem>) {
        callback.onResult(loadChunk(params.startPosition, params.loadSize))
    }

    private fun loadChunk(offset: Int, limit: Int): List<DisplayableItem> {
        return chunked.chunkOf(ChunkRequest(offset = offset, limit = limit))
            .map { it.toTabDisplayableItem(resources) }
    }

}

internal class PodcastArtistDataSourceFactory @Inject constructor(
    private val dataSource: Provider<PodcastArtistDataSource>
) : DataSource.Factory<Int, DisplayableItem>() {

    override fun create(): DataSource<Int, DisplayableItem> {
        return dataSource.get()
    }
}