package dev.olog.msc.presentation.tabs.paging.track

import android.content.Context
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.gateway.track.GenreGateway
import dev.olog.msc.presentation.base.list.model.DisplayableItem
import dev.olog.msc.presentation.base.list.paging.BaseDataSource
import dev.olog.msc.presentation.base.list.paging.BaseDataSourceFactory
import dev.olog.msc.presentation.tabs.mapper.toTabDisplayableItem
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

internal class GenreDataSource @Inject constructor(
    @ApplicationContext context: Context,
    gateway: GenreGateway
) : BaseDataSource<DisplayableItem>() {
    private val resources = context.resources
    private val chunked = gateway.getAll()

    override fun onAttach() {
        launch {
            chunked.observeNotification()
                .take(1)
                .collect {
                    invalidate()
                }
        }
    }

    override suspend fun getMainDataSize(): Int {
        return chunked.getCount(Filter.NO_FILTER)
    }

    override suspend fun getHeaders(mainListSize: Int): List<DisplayableItem> = listOf()

    override suspend fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun loadInternal(request: Request): List<DisplayableItem> {
        return chunked.getPage(request)
            .map { it.toTabDisplayableItem(resources) }
    }

}

internal class GenreDataSourceFactory @Inject constructor(
    dataSourceProvider: Provider<GenreDataSource>
) : BaseDataSourceFactory<DisplayableItem, GenreDataSource>(dataSourceProvider)