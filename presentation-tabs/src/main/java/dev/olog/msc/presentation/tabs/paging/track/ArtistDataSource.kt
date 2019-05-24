package dev.olog.msc.presentation.tabs.paging.track

import android.content.res.Resources
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.gateway.prefs.SortPreferencesGateway
import dev.olog.msc.core.gateway.track.ArtistGateway
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.base.paging.BaseDataSource
import dev.olog.msc.presentation.base.paging.BaseDataSourceFactory
import dev.olog.msc.presentation.tabs.TabFragmentHeaders
import dev.olog.msc.presentation.tabs.mapper.toTabDisplayableItem
import dev.olog.msc.shared.core.flow.merge
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

internal class ArtistDataSource @Inject constructor(
    private val resources: Resources,
    private val gateway: ArtistGateway,
    private val prefsGateway: SortPreferencesGateway,
    private val displayableHeaders: TabFragmentHeaders
) : BaseDataSource<DisplayableItem>() {

    private val chunked = gateway.getAll()

    override fun onAttach() {
        launch {
            chunked.observeNotification()
                .merge(prefsGateway.observeAllArtistsSortOrder().drop(1))
                .take(1)
                .collect {
                    invalidate()
                }
        }
    }

    override fun getHeaders(mainListSize: Int): List<DisplayableItem> {
        val headers = mutableListOf<DisplayableItem>()
        if (gateway.canShowRecentlyAdded(Filter.NO_FILTER)) {
            headers.addAll(displayableHeaders.recentlyAddedArtistsHeaders)
        }
        if (gateway.canShowLastPlayed()) {
            headers.addAll(displayableHeaders.lastPlayedArtistHeaders)
        }
        if (headers.isNotEmpty()) {
            headers.addAll(displayableHeaders.allArtistsHeader)
        }
        return headers
    }

    override fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun getMainDataSize(): Int {
        return chunked.getCount(Filter.NO_FILTER)
    }

    override fun loadInternal(request: Request): List<DisplayableItem> {
        return chunked.getPage(request)
            .map { it.toTabDisplayableItem(resources) }
    }

}

internal class ArtistDataSourceFactory @Inject constructor(
    dataSourceProvider: Provider<ArtistDataSource>
) : BaseDataSourceFactory<DisplayableItem, ArtistDataSource>(dataSourceProvider)