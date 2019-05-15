package dev.olog.msc.presentation.tabs.paging.last.played

import androidx.lifecycle.Lifecycle
import androidx.paging.DataSource
import dev.olog.msc.core.dagger.qualifier.ActivityLifecycle
import dev.olog.msc.core.entity.ChunkRequest
import dev.olog.msc.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.base.paging.BaseDataSource
import dev.olog.msc.presentation.tabs.mapper.toTabLastPlayedDisplayableItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

internal class LastPlayedPodcastAlbumDataSource @Inject constructor(
    @ActivityLifecycle lifecycle: Lifecycle,
    private val gateway: PodcastAlbumGateway
) : BaseDataSource<DisplayableItem>() {

    private val chunked = gateway.getChunk()

    init {
        launch(Dispatchers.Main) { lifecycle.addObserver(this@LastPlayedPodcastAlbumDataSource) }
        launch {
            chunked.observeChanges()
                .take(1)
                .collect {
                    invalidate()
                }
        }
    }

    override val canLoadData: Boolean
        get() = gateway.canShowLastPlayed()

    override fun getMainDataSize(): Int {
        return chunked.allDataSize
    }

    override fun getHeaders(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun loadInternal(chunkRequest: ChunkRequest): List<DisplayableItem> {
        return chunked.chunkOf(chunkRequest)
            .map { it.toTabLastPlayedDisplayableItem() }
    }

}

internal class LastPlayedPodcastAlbumDataSourceFactory @Inject constructor(
    private val dataSource: Provider<LastPlayedPodcastAlbumDataSource>
) : DataSource.Factory<Int, DisplayableItem>() {

    override fun create(): DataSource<Int, DisplayableItem> {
        return dataSource.get()
    }
}