package dev.olog.msc.presentation.create.playlist

import androidx.lifecycle.Lifecycle
import androidx.paging.DataSource
import dev.olog.msc.core.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.core.entity.Page
import dev.olog.msc.core.entity.PlaylistType
import dev.olog.msc.core.gateway.SearchGateway
import dev.olog.msc.core.gateway.SearchGateway.By
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.base.paging.BaseDataSource
import dev.olog.msc.presentation.create.playlist.model.toDisplayableItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider

class CreatePlaylistDataSource @Inject constructor(
    @FragmentLifecycle lifecycle: Lifecycle,
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

    init {
        launch {
            withContext(Dispatchers.Main) { lifecycle.addObserver(this@CreatePlaylistDataSource) }
            searchGateway.searchSongsAndPocastsBy(SearchGateway.SearchRequest(byWord = "" to arrayOf())) // not important
                .observeNotification()
                .collect {
                    invalidate()
                }
        }
    }

    override fun getMainDataSize(): Int {
        if (playlistType == PlaylistType.TRACK) {
            return searchGateway.searchSongOnlyBy(searchRequest).getCount()
        } else {
            return searchGateway.searchPodcastOnlyBy(searchRequest).getCount()
        }
    }

    override fun loadInternal(page: Page): List<DisplayableItem> {
        if (playlistType == PlaylistType.TRACK) {
            return searchGateway.searchSongOnlyBy(searchRequest).getPage(page)
                .map { it.toDisplayableItem() }
        } else {
            return searchGateway.searchPodcastOnlyBy(searchRequest).getPage(page)
                .map { it.toDisplayableItem() }
        }
    }

    override fun getHeaders(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

}

class CreatePlaylistDataSourceFactory @Inject constructor(
    private val dataSourceProvider: Provider<CreatePlaylistDataSource>
) : DataSource.Factory<Int, DisplayableItem>() {

    lateinit var playlistType: PlaylistType
    private var filterBy: String = ""
    private var dataSource: CreatePlaylistDataSource? = null
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
        val dataSource = dataSourceProvider.get()
        this.dataSource = dataSource
        dataSource.filterBy = this.filterBy
        dataSource.playlistType = this.playlistType
        dataSource.selectedIds = this.selectedIds
        return dataSource
    }
}