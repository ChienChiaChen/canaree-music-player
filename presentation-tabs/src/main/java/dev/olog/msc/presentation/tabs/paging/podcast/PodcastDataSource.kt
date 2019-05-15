package dev.olog.msc.presentation.tabs.paging.podcast

import android.content.res.Resources
import androidx.lifecycle.Lifecycle
import androidx.paging.DataSource
import dev.olog.msc.core.dagger.qualifier.ActivityLifecycle
import dev.olog.msc.core.entity.ChunkRequest
import dev.olog.msc.core.gateway.podcast.PodcastGateway
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.base.paging.BaseDataSource
import dev.olog.msc.presentation.tabs.mapper.toTabDisplayableItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

internal class PodcastDataSource @Inject constructor(
    @ActivityLifecycle lifecycle: Lifecycle,
    private val resources: Resources,
    gateway: PodcastGateway
) : BaseDataSource<DisplayableItem>() {

    private val chunked = gateway.getChunk()

    init {
        launch(Dispatchers.Main) { lifecycle.addObserver(this@PodcastDataSource) }
        launch {
            chunked.observeChanges()
                .take(1)
                .collect {
                    invalidate()
                }
        }
    }

    override fun getMainDataSize(): Int {
        return chunked.allDataSize
    }

    override fun getHeaders(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun loadInternal(chunkRequest: ChunkRequest): List<DisplayableItem> {
        return chunked.chunkOf(chunkRequest)
            .map { it.toTabDisplayableItem(resources) }
    }


}

internal class PodcastDataSourceFactory @Inject constructor(
    private val dataSource: Provider<PodcastDataSource>
) : DataSource.Factory<Int, DisplayableItem>() {

    override fun create(): DataSource<Int, DisplayableItem> {
        return dataSource.get()
    }
}