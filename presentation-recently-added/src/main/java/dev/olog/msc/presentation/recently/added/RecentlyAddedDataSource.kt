package dev.olog.msc.presentation.recently.added

import androidx.lifecycle.Lifecycle
import androidx.paging.DataSource
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.interactor.played.GetRecentlyAddedSongsUseCase
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.base.paging.BaseDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider

internal class RecentlyAddedDataSource @Inject constructor(
    @FragmentLifecycle lifecycle: Lifecycle,
    private val recentlyAddedUseCase: GetRecentlyAddedSongsUseCase,
    private val mediaId: MediaId
) : BaseDataSource<DisplayableItem>() {

    private val chunked by lazy { recentlyAddedUseCase.get(mediaId) }

    init {
        launch {
            withContext(Dispatchers.Main) { lifecycle.addObserver(this@RecentlyAddedDataSource) }
            chunked.observeNotification()
                .take(1)
                .collect {
                    invalidate()
                }
        }
    }

    override fun getMainDataSize(): Int {
        return chunked.getCount(Filter.NO_FILTER)
    }

    override fun getHeaders(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun loadInternal(request: Request): List<DisplayableItem> {
        return chunked.getPage(request)
            .map { it.toRecentDetailDisplayableItem(mediaId) }
    }

    private fun Song.toRecentDetailDisplayableItem(parentId: MediaId): DisplayableItem {
        return DisplayableItem(
            R.layout.item_recently_added,
            MediaId.playableItem(parentId, id),
            title,
            artist,
            image,
            true
        )
    }


}

internal class RecentlyAddedDataSourceFactory @Inject constructor(
    private val dataSource: Provider<RecentlyAddedDataSource>
) : DataSource.Factory<Int, DisplayableItem>() {

    override fun create(): DataSource<Int, DisplayableItem> {
        return dataSource.get()
    }
}