package dev.olog.msc.presentation.tabs.paging.last.played

import android.content.res.Resources
import androidx.lifecycle.Lifecycle
import androidx.paging.DataSource
import dev.olog.msc.core.dagger.qualifier.ActivityLifecycle
import dev.olog.msc.core.entity.Page
import dev.olog.msc.core.gateway.podcast.PodcastArtistGateway
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.base.paging.BaseDataSource
import dev.olog.msc.presentation.tabs.mapper.toTabLastPlayedDisplayableItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider

internal class LastPlayedPodcastArtistDataSource @Inject constructor(
    @ActivityLifecycle lifecycle: Lifecycle,
    private val resources: Resources,
    private val gateway: PodcastArtistGateway
) : BaseDataSource<DisplayableItem>() {

    private val chunked = gateway.getAll()

    init {
        launch {
            withContext(Dispatchers.Main) { lifecycle.addObserver(this@LastPlayedPodcastArtistDataSource) }
            chunked.observeNotification()
                .take(1)
                .collect {
                    invalidate()
                }
        }
    }

    override val canLoadData: Boolean
        get() = gateway.canShowLastPlayed()

    override fun getMainDataSize(): Int {
        return chunked.getCount()
    }

    override fun getHeaders(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun loadInternal(page: Page): List<DisplayableItem> {
        return chunked.getPage(page)
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