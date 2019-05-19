package dev.olog.msc.presentation.search.paging

import androidx.lifecycle.Lifecycle
import androidx.paging.DataSource
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.entity.data.request.with
import dev.olog.msc.core.entity.podcast.PodcastPlaylist
import dev.olog.msc.core.entity.track.Playlist
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.base.paging.BaseDataSource
import dev.olog.msc.presentation.search.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider

class SearchPlaylistsDataSource @Inject constructor(
    @FragmentLifecycle lifecycle: Lifecycle,
    private val gateway: PlaylistGateway
) : BaseDataSource<DisplayableItem>() {

    var filterBy: String = ""

    private val chunk = gateway.getAll()

    init {
        launch {
            withContext(Dispatchers.Main) { lifecycle.addObserver(this@SearchPlaylistsDataSource) }
            gateway.getAll().observeNotification()
                .collect { invalidate() }
        }
    }

    private val filterRequest by lazy { Filter(filterBy, arrayOf(Filter.By.TITLE), Filter.BehaviorOnEmpty.NONE) }


    override fun getMainDataSize(): Int {
        return chunk.getCount(filterRequest)
    }

    override fun getHeaders(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun loadInternal(request: Request): List<DisplayableItem> {
        return chunk.getPage(request.with(filter = filterRequest))
            .map { it.toSearchDisplayableItem() }
    }

    private fun Playlist.toSearchDisplayableItem(): DisplayableItem {
        return DisplayableItem(
            R.layout.item_search_album,
            MediaId.playlistId(id),
            title,
            null
        )
    }

    private fun PodcastPlaylist.toSearchDisplayableItem(): DisplayableItem {
        return DisplayableItem(
            R.layout.item_search_album,
            MediaId.podcastPlaylistId(id),
            title,
            null
        )
    }


}

internal class SearchPlaylistsDataSourceFactory @Inject constructor(
    private val dataSourceProvider: Provider<SearchPlaylistsDataSource>
) : DataSource.Factory<Int, DisplayableItem>() {

    private var filterBy: String = ""
    private var dataSource: SearchPlaylistsDataSource? = null

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

    fun invalidate() {
        dataSource?.invalidate()
    }
}