package dev.olog.msc.presentation.tabs.paging.track

import androidx.lifecycle.Lifecycle
import androidx.paging.DataSource
import dev.olog.msc.core.dagger.qualifier.ActivityLifecycle
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.SongGateway
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.tabs.TabFragmentHeaders
import dev.olog.msc.presentation.tabs.mapper.toTabDisplayableItem
import dev.olog.msc.presentation.tabs.paging.BaseDataSource
import dev.olog.msc.shared.extensions.startWithIfNotEmpty
import javax.inject.Inject
import javax.inject.Provider

internal class SongDataSource @Inject constructor(
    @ActivityLifecycle lifecycle: Lifecycle,
    gateway: SongGateway,
    private val headers: TabFragmentHeaders
) : BaseDataSource<Song, DisplayableItem>(lifecycle, gateway) {

    override fun enrichInitialData(original: List<DisplayableItem>): List<DisplayableItem> {
        return original.startWithIfNotEmpty(headers.shuffleHeader)
    }

    override fun mapData(original: Song): DisplayableItem {
        return original.toTabDisplayableItem()
    }

    override fun headerSize(): Int = 1

}

internal class SongDataSourceFactory @Inject constructor(
    private val dataSource: Provider<SongDataSource>
) : DataSource.Factory<Int, DisplayableItem>() {

    override fun create(): DataSource<Int, DisplayableItem> {
        return dataSource.get()
    }
}