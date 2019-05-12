package dev.olog.msc.presentation.tabs.paging.last.played

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
import dev.olog.msc.presentation.tabs.mapper.toTabLastPlayedDisplayableItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

internal class LastPlayedPodcastArtistDataSource @Inject constructor(
    @ActivityLifecycle lifecycle: Lifecycle,
    private val resources: Resources,
    private val gateway: PodcastArtistGateway
) : PositionalDataSource<DisplayableItem>(), DefaultLifecycleObserver, CoroutineScope by CustomScope() {

    private val chunked = gateway.getLastPlayedChunk()

    init {
        launch(Dispatchers.Main) { lifecycle.addObserver(this@LastPlayedPodcastArtistDataSource) }
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
        if (gateway.canShowLastPlayed()) {
            val initialChunk = loadChunk(params.requestedStartPosition, params.requestedLoadSize)
            val count = chunked.allDataSize
            callback.onResult(initialChunk, 0, count)
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<DisplayableItem>) {
        if (gateway.canShowLastPlayed()) {
            callback.onResult(loadChunk(params.startPosition, params.loadSize))
        }
    }

    private fun loadChunk(offset: Int, limit: Int): List<DisplayableItem> {
        return chunked.chunkOf(ChunkRequest(offset = offset, limit = limit))
            .map { it.toTabLastPlayedDisplayableItem(resources) }
    }

}

internal class LastPlayedPodcastArtistDataSourceFactory @Inject constructor(
    private val dataSource: Provider<LastPlayedPodcastArtistDataSource>
) : DataSource.Factory<Int, DisplayableItem>() {

    override fun create(): DataSource<Int, DisplayableItem> {
        return dataSource.get()
    }
}