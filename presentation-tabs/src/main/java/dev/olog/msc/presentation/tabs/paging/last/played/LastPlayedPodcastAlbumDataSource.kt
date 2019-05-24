package dev.olog.msc.presentation.tabs.paging.last.played

import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.msc.presentation.base.list.model.DisplayableItem
import dev.olog.msc.presentation.base.list.paging.BaseDataSource
import dev.olog.msc.presentation.base.list.paging.BaseDataSourceFactory
import dev.olog.msc.presentation.tabs.mapper.toTabLastPlayedDisplayableItem
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

internal class LastPlayedPodcastAlbumDataSource @Inject constructor(
    private val gateway: PodcastAlbumGateway
) : BaseDataSource<DisplayableItem>() {

    private val chunked = gateway.getLastPlayed()

    override fun onAttach() {
        launch {
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
        return chunked.getCount(Filter.NO_FILTER)
    }

    override fun getHeaders(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun loadInternal(request: Request): List<DisplayableItem> {
        return chunked.getPage(request)
            .map { it.toTabLastPlayedDisplayableItem() }
    }

}

internal class LastPlayedPodcastAlbumDataSourceFactory @Inject constructor(
    dataSourceProvider: Provider<LastPlayedPodcastAlbumDataSource>
) : BaseDataSourceFactory<DisplayableItem, LastPlayedPodcastAlbumDataSource>(dataSourceProvider)