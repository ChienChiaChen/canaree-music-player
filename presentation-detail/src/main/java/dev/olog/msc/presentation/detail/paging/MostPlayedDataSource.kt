package dev.olog.msc.presentation.detail.paging

import androidx.lifecycle.Lifecycle
import androidx.paging.DataSource
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.interactor.played.GetMostPlayedSongsUseCase
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.base.paging.BaseDataSource
import dev.olog.msc.presentation.detail.mapper.toMostPlayedDetailDisplayableItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider

internal class MostPlayedDataSource @Inject constructor(
    @FragmentLifecycle lifecycle: Lifecycle,
    private val mostPlayedUseCase: GetMostPlayedSongsUseCase,
    private val mediaId: MediaId
) : BaseDataSource<DisplayableItem>() {

    private val chunked by lazy { mostPlayedUseCase.get(mediaId) }

    init {
        launch {
            withContext(Dispatchers.Main) { lifecycle.addObserver(this@MostPlayedDataSource) }
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

    override fun loadInternal(page: Request): List<DisplayableItem> {
        return chunked.getPage(page)
            .map { it.toMostPlayedDetailDisplayableItem(mediaId) }
    }
}

internal class MostPlayedDataSourceFactory @Inject constructor(
    private val dataSource: Provider<MostPlayedDataSource>
) : DataSource.Factory<Int, DisplayableItem>() {

    override fun create(): DataSource<Int, DisplayableItem> {
        return dataSource.get()
    }
}