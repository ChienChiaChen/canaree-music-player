package dev.olog.msc.presentation.tabs.paging.track

import androidx.lifecycle.Lifecycle
import androidx.paging.DataSource
import dev.olog.msc.core.coroutines.merge
import dev.olog.msc.core.dagger.qualifier.ActivityLifecycle
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.track.SongGateway
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.base.paging.BaseDataSource
import dev.olog.msc.presentation.tabs.TabFragmentHeaders
import dev.olog.msc.presentation.tabs.mapper.toTabDisplayableItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider

internal class SongDataSource @Inject constructor(
    @ActivityLifecycle lifecycle: Lifecycle,
    gateway: SongGateway,
    prefsGateway: AppPreferencesGateway,
    private val displayableHeaders: TabFragmentHeaders
) : BaseDataSource<DisplayableItem>() {

    private val pageRequest = gateway.getAll()

    init {
        launch {
            withContext(Dispatchers.Main) { lifecycle.addObserver(this@SongDataSource) }
            pageRequest.observeNotification()
                .merge(prefsGateway.observeAllTracksSortOrder().drop(1))
                .take(1)
                .collect {
                    invalidate()
                }
        }
    }

    override fun getMainDataSize(): Int {
        return pageRequest.getCount(Filter.NO_FILTER)
    }

    override fun getHeaders(mainListSize: Int): List<DisplayableItem> {
        if (mainListSize == 0) {
            return listOf()
        }
        return listOf(displayableHeaders.shuffleHeader)
    }

    override fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun loadInternal(request: Request): List<DisplayableItem> {
        return pageRequest.getPage(request)
            .map { it.toTabDisplayableItem() }
    }

}

internal class SongDataSourceFactory @Inject constructor(
    private val dataSource: Provider<SongDataSource>
) : DataSource.Factory<Int, DisplayableItem>() {

    override fun create(): DataSource<Int, DisplayableItem> {
        return dataSource.get()
    }
}