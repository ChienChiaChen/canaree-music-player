package dev.olog.msc.presentation.tabs.paging.podcast

import android.content.res.Resources
import androidx.lifecycle.Lifecycle
import androidx.paging.DataSource
import dev.olog.msc.core.dagger.qualifier.ActivityLifecycle
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.gateway.PodcastGateway
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.tabs.mapper.toTabDisplayableItem
import dev.olog.msc.presentation.tabs.paging.BaseDataSource
import javax.inject.Inject
import javax.inject.Provider

internal class PodcastDataSource @Inject constructor(
    @ActivityLifecycle lifecycle: Lifecycle,
    private val resources: Resources,
    gateway: PodcastGateway
) : BaseDataSource<Podcast, DisplayableItem>(lifecycle, gateway) {

    override fun enrichInitialData(original: List<DisplayableItem>): List<DisplayableItem> {
        return original
    }

    override fun mapData(original: Podcast): DisplayableItem {
        return original.toTabDisplayableItem(resources)
    }

}

internal class PodcastDataSourceFactory @Inject constructor(
    private val dataSource: Provider<PodcastDataSource>
) : DataSource.Factory<Int, DisplayableItem>() {

    override fun create(): DataSource<Int, DisplayableItem> {
        return dataSource.get()
    }
}