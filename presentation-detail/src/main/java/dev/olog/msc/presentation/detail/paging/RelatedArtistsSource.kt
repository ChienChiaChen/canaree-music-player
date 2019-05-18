package dev.olog.msc.presentation.detail.paging

import android.content.res.Resources
import androidx.lifecycle.Lifecycle
import androidx.paging.DataSource
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.entity.data.request.with
import dev.olog.msc.core.interactor.GetRelatedArtistsUseCase
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.base.paging.BaseDataSource
import dev.olog.msc.presentation.detail.DetailFragmentViewModel.Companion.RELATED_ARTISTS_TO_SEE
import dev.olog.msc.presentation.detail.mapper.toRelatedArtist
import dev.olog.msc.shared.utils.clamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider

internal class RelatedArtistsSource @Inject constructor(
    @FragmentLifecycle lifecycle: Lifecycle,
    private val resources: Resources,
    private val relatedArtistsUseCase: GetRelatedArtistsUseCase,
    private val mediaId: MediaId

) : BaseDataSource<DisplayableItem>() {

    private val chunked by lazy { relatedArtistsUseCase.get(mediaId) }

    init {
        launch {
            withContext(Dispatchers.Main) { lifecycle.addObserver(this@RelatedArtistsSource) }
            if (canLoadData) {
                chunked.observeNotification()
                    .take(1)
                    .collect {
                        invalidate()
                    }
            }
        }
    }

    var filterBy: String = ""
    private val filterRequest by lazy { Filter(filterBy, arrayOf(Filter.By.ARTIST)) }

    override val canLoadData: Boolean
        get() = relatedArtistsUseCase.canShow(mediaId, filterRequest)

    override fun getMainDataSize(): Int {
        return clamp(chunked.getCount(filterRequest), 0, RELATED_ARTISTS_TO_SEE)
    }

    override fun getHeaders(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun loadInternal(request: Request): List<DisplayableItem> {
        return chunked.getPage(request.with(filter = filterRequest))
            .map { it.toRelatedArtist(resources) }
    }
}

internal class RelatedArtistsSourceFactory @Inject constructor(
    private val dataSourceProvider: Provider<RelatedArtistsSource>
) : DataSource.Factory<Int, DisplayableItem>() {

    private var filterBy: String = ""
    private var dataSource: RelatedArtistsSource? = null

    fun updateFilterBy(filterBy: String) {
        if (this.filterBy != filterBy) {
            this.filterBy = filterBy
            dataSource?.invalidate()
        }
    }

    override fun create(): DataSource<Int, DisplayableItem> {
        val dataSource = dataSourceProvider.get()
        this.dataSource = dataSource
        dataSource.filterBy = filterBy
        return dataSource
    }
}