package dev.olog.msc.presentation.tabs.paging.track

import android.content.res.Resources
import androidx.lifecycle.Lifecycle
import androidx.paging.DataSource
import dev.olog.msc.core.dagger.qualifier.ActivityLifecycle
import dev.olog.msc.core.entity.track.Folder
import dev.olog.msc.core.gateway.FolderGateway
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.tabs.mapper.toTabDisplayableItem
import dev.olog.msc.presentation.tabs.paging.BaseDataSource
import javax.inject.Inject
import javax.inject.Provider

internal class FolderDataSource @Inject constructor(
    @ActivityLifecycle lifecycle: Lifecycle,
    private val resources: Resources,
    gateway: FolderGateway
) : BaseDataSource<Folder, DisplayableItem>(lifecycle, gateway) {

    override fun enrichInitialData(original: List<DisplayableItem>): List<DisplayableItem> {
        return original
    }

    override fun mapData(original: Folder): DisplayableItem {
        return original.toTabDisplayableItem(resources)
    }

}

internal class FolderDataSourceFactory @Inject constructor(
    private val dataSource: Provider<FolderDataSource>
) : DataSource.Factory<Int, DisplayableItem>() {

    override fun create(): DataSource<Int, DisplayableItem> {
        return dataSource.get()
    }
}