package dev.olog.msc.presentation.create.playlist

import androidx.paging.DataSource
import dev.olog.msc.core.entity.PlaylistType
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.gateway.SearchGateway
import dev.olog.msc.core.gateway.SearchGateway.By
import dev.olog.msc.presentation.base.list.model.DisplayableItem
import dev.olog.msc.presentation.base.list.paging.BaseDataSource
import dev.olog.msc.presentation.base.list.paging.BaseDataSourceFactory
import dev.olog.msc.presentation.create.playlist.model.toDisplayableItem
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class CreatePlaylistDataSource @Inject constructor(
    private val searchGateway: SearchGateway
) : BaseDataSource<DisplayableItem>() {

    lateinit var playlistType: PlaylistType
    var filterBy: String = ""
    var selectedIds: List<Long>? = null

    private val searchRequest by lazy {
        SearchGateway.SearchRequest(
            byWord = filterBy to arrayOf(By.TITLE, By.ARTIST, By.ALBUM),
            byIds = selectedIds
        )
    }

    override fun onAttach() {
        launch {
            searchGateway.searchSongsAndPocastsBy(SearchGateway.SearchRequest(byWord = "" to arrayOf())) // not important
                .observeNotification()
                .collect {
                    invalidate()
                }
        }
    }

    override suspend fun getMainDataSize(): Int {

        if (playlistType == PlaylistType.TRACK) {
            return searchGateway.searchSongOnlyBy(searchRequest).getCount(Filter.NO_FILTER)
        } else {
            return searchGateway.searchPodcastOnlyBy(searchRequest).getCount(Filter.NO_FILTER)
        }
    }

    override fun loadInternal(request: Request): List<DisplayableItem> {
        if (playlistType == PlaylistType.TRACK) {
            return searchGateway.searchSongOnlyBy(searchRequest).getPage(request)
                .map { it.toDisplayableItem() }
        } else {
            return searchGateway.searchPodcastOnlyBy(searchRequest).getPage(request)
                .map { it.toDisplayableItem() }
        }
    }

    override suspend fun getHeaders(mainListSize: Int): List<DisplayableItem> = listOf()

    override suspend fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

}

class CreatePlaylistDataSourceFactory @Inject constructor(
    dataSourceProvider: Provider<CreatePlaylistDataSource>
) : BaseDataSourceFactory<DisplayableItem, CreatePlaylistDataSource>(dataSourceProvider) {

    lateinit var playlistType: PlaylistType
    private var filterBy: String = ""
    private var selectedIds: List<Long>? = listOf()

    fun updateFilterBy(filterBy: String) {
        if (this.filterBy != filterBy) {
            this.filterBy = filterBy
            dataSource?.invalidate()
        }
    }

    fun updateSelectedIds(selectedIds: List<Long>?) {
        if (this.selectedIds != selectedIds) {
            this.selectedIds = selectedIds
            dataSource?.invalidate()
        }
    }

    override fun create(): DataSource<Int, DisplayableItem> {
        dataSource?.onDetach()
        dataSource = dataSourceProvider.get()
        dataSource!!.onAttach()
        dataSource!!.filterBy = this.filterBy
        dataSource!!.playlistType = this.playlistType
        dataSource!!.selectedIds = this.selectedIds
        return dataSource!!
    }
}