package dev.olog.msc.presentation.tabs.paging.track

import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.gateway.prefs.SortPreferencesGateway
import dev.olog.msc.core.gateway.track.AlbumGateway
import dev.olog.msc.presentation.base.list.model.DisplayableItem
import dev.olog.msc.presentation.base.list.paging.BaseDataSource
import dev.olog.msc.presentation.base.list.paging.BaseDataSourceFactory
import dev.olog.msc.presentation.tabs.TabFragmentHeaders
import dev.olog.msc.presentation.tabs.mapper.toTabDisplayableItem
import dev.olog.msc.shared.core.flow.merge
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

internal class AlbumDataSource @Inject constructor(
    private val gateway: AlbumGateway,
    private val prefsGateway: SortPreferencesGateway,
    private val displayableHeaders: TabFragmentHeaders
) : BaseDataSource<DisplayableItem>() {

    private val page = gateway.getAll()

    override fun onAttach() {
        launch {
            page.observeNotification()
                .merge(prefsGateway.observeAllAlbumsSortOrder().drop(1))
                .take(1)
                .collect {
                    invalidate()
                }
        }
        // TODO observe recently played changes from none to someyhing, same in artistdatasource and podcasts
    }

    override suspend fun getMainDataSize(): Int {
        return page.getCount(Filter.NO_FILTER)
    }

    override suspend fun getHeaders(mainListSize: Int): List<DisplayableItem> {
        val headers = loadParallel(
            async {
                if (gateway.canShowRecentlyAdded(Filter.NO_FILTER)) {
                    return@async displayableHeaders.recentlyAddedAlbumsHeaders
                }
                emptyList<DisplayableItem>()
            },
            async {
                if (gateway.canShowLastPlayed()) {
                    return@async displayableHeaders.lastPlayedAlbumHeaders
                }
                emptyList<DisplayableItem>()
            }
        )

        if (headers.isNotEmpty()) {
            return headers.plus(displayableHeaders.allAlbumsHeader)
        }
        return emptyList()
    }

    override suspend fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun loadInternal(request: Request): List<DisplayableItem> {
        return this.page.getPage(request)
            .map { it.toTabDisplayableItem() }
    }

}

internal class AlbumDataSourceFactory @Inject constructor(
    dataSourceProvider: Provider<AlbumDataSource>
) : BaseDataSourceFactory<DisplayableItem, AlbumDataSource>(dataSourceProvider)