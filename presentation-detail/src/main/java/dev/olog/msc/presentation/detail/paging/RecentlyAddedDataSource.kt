package dev.olog.msc.presentation.detail.paging

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.interactor.played.GetRecentlyAddedSongsUseCase
import dev.olog.msc.presentation.base.list.model.DisplayableItem
import dev.olog.msc.presentation.base.list.paging.BaseDataSource
import dev.olog.msc.presentation.base.list.paging.BaseDataSourceFactory
import dev.olog.msc.presentation.detail.DetailFragmentViewModel
import dev.olog.msc.presentation.detail.mapper.toRecentDetailDisplayableItem
import dev.olog.msc.shared.utils.clamp
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

internal class RecentlyAddedDataSource @Inject constructor(
    private val recentlyAddedUseCase: GetRecentlyAddedSongsUseCase,
    private val mediaId: MediaId
) : BaseDataSource<DisplayableItem>() {

    private val chunked by lazy { recentlyAddedUseCase.get(mediaId) }

    override fun onAttach() {
        launch {
            if (canLoadData) {
                chunked.observeNotification()
                    .take(1)
                    .collect {
                        invalidate()
                    }
            }
        }
    }

    override val canLoadData: Boolean
        get() = recentlyAddedUseCase.canShow(mediaId)

    override fun getMainDataSize(): Int {
        return clamp(chunked.getCount(Filter.NO_FILTER), 0, DetailFragmentViewModel.RECENTLY_ADDED_VISIBLE_PAGES)
    }

    override fun getHeaders(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun loadInternal(request: Request): List<DisplayableItem> {
        return chunked.getPage(request)
            .map { it.toRecentDetailDisplayableItem(mediaId) }
    }
}

internal class RecentlyAddedDataSourceFactory @Inject constructor(
    dataSourceProvider: Provider<RecentlyAddedDataSource>
) : BaseDataSourceFactory<DisplayableItem, RecentlyAddedDataSource>(dataSourceProvider)