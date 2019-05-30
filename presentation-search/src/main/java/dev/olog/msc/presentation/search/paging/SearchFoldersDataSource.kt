package dev.olog.msc.presentation.search.paging

import androidx.paging.DataSource
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.entity.data.request.with
import dev.olog.msc.core.entity.track.Folder
import dev.olog.msc.core.gateway.track.FolderGateway
import dev.olog.msc.presentation.base.list.model.DisplayableItem
import dev.olog.msc.presentation.base.list.paging.BaseDataSource
import dev.olog.msc.presentation.base.list.paging.BaseDataSourceFactory
import dev.olog.msc.presentation.search.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class SearchFoldersDataSource @Inject constructor(
    private val gateway: FolderGateway
) : BaseDataSource<DisplayableItem>() {

    var filterBy: String = ""

    private val chunk = gateway.getAll()

    override fun onAttach() {
        launch {
            gateway.getAll().observeNotification()
                .collect { invalidate() }
        }
    }

    private val filterRequest by lazy { Filter(filterBy, arrayOf(Filter.By.TITLE), Filter.BehaviorOnEmpty.NONE) }


    override suspend fun getMainDataSize(): Int {
        return chunk.getCount(filterRequest)
    }

    override suspend fun getHeaders(mainListSize: Int): List<DisplayableItem> = listOf()

    override suspend fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun loadInternal(request: Request): List<DisplayableItem> {
        return chunk.getPage(request.with(filter = filterRequest))
            .map { it.toSearchDisplayableItem() }
    }

    private fun Folder.toSearchDisplayableItem(): DisplayableItem {
        return DisplayableItem(
            R.layout.item_search_album,
            MediaId.folderId(path),
            title,
            null
        )
    }

}

internal class SearchFoldersDataSourceFactory @Inject constructor(
    dataSourceProvider: Provider<SearchFoldersDataSource>
) : BaseDataSourceFactory<DisplayableItem, SearchFoldersDataSource>(dataSourceProvider) {

    private var filterBy: String = ""

    fun updateFilterBy(filterBy: String) {
        if (this.filterBy != filterBy) {
            this.filterBy = filterBy
            dataSource?.invalidate()
        }
    }

    override fun create(): DataSource<Int, DisplayableItem> {
        dataSource?.onDetach()
        dataSource = dataSourceProvider.get()
        dataSource!!.onAttach()
        dataSource!!.filterBy = this.filterBy
        return dataSource!!
    }

    fun invalidate() {
        dataSource?.invalidate()
    }
}