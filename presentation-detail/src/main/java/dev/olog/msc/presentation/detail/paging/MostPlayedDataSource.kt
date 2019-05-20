package dev.olog.msc.presentation.detail.paging

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.interactor.played.GetMostPlayedSongsUseCase
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.base.paging.BaseDataSource
import dev.olog.msc.presentation.base.paging.BaseDataSourceFactory
import dev.olog.msc.presentation.detail.mapper.toMostPlayedDetailDisplayableItem
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

internal class MostPlayedDataSource @Inject constructor(
    private val mostPlayedUseCase: GetMostPlayedSongsUseCase,
    private val mediaId: MediaId
) : BaseDataSource<DisplayableItem>() {

    private val chunked by lazy { mostPlayedUseCase.get(mediaId) }

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
        get() = mostPlayedUseCase.canShow(mediaId)

    override fun getMainDataSize(): Int {
        return chunked.getCount(Filter.NO_FILTER)
    }

    override fun getHeaders(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun loadInternal(request: Request): List<DisplayableItem> {
        return chunked.getPage(request)
            .mapIndexed { index: Int, song: Song -> song.toMostPlayedDetailDisplayableItem(mediaId, index) }
    }
}

internal class MostPlayedDataSourceFactory @Inject constructor(
    dataSourceProvider: Provider<MostPlayedDataSource>
) : BaseDataSourceFactory<DisplayableItem, MostPlayedDataSource>(dataSourceProvider)