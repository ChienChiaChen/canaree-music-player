package dev.olog.msc.presentation.search.paging

import androidx.paging.DataSource
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.SearchFilters
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.entity.data.request.with
import dev.olog.msc.core.entity.podcast.PodcastPlaylist
import dev.olog.msc.core.entity.track.Playlist
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.presentation.base.list.model.DisplayableItem
import dev.olog.msc.presentation.base.list.paging.BaseDataSource
import dev.olog.msc.presentation.base.list.paging.BaseDataSourceFactory
import dev.olog.msc.presentation.search.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class SearchPlaylistsDataSource @Inject constructor(
        private val gateway: PlaylistGateway,
        private val podcastGateway: PodcastPlaylistGateway,
        prefsGateway: AppPreferencesGateway
) : BaseDataSource<DisplayableItem>() {

    var filterBy: String = ""

    private val filters = prefsGateway.getSearchFilters()
    private val podcastsOnly = filters.contains(SearchFilters.PODCAST)

    override fun onAttach() {
        launch {
            if (podcastsOnly) {
                podcastGateway.getAll().observeNotification()
                        .collect { invalidate() }
            } else {
                gateway.getAll().observeNotification()
                        .collect { invalidate() }
            }
        }
    }

    private val filterRequest by lazy { Filter(filterBy, arrayOf(Filter.By.TITLE), Filter.BehaviorOnEmpty.NONE) }


    override suspend fun getMainDataSize(): Int {
        if (podcastsOnly){
            return podcastGateway.getAll().getCount(filterRequest)
        }
        return gateway.getAll().getCount(filterRequest)
    }

    override suspend fun getHeaders(mainListSize: Int): List<DisplayableItem> = listOf()

    override suspend fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun loadInternal(request: Request): List<DisplayableItem> {
        if (podcastsOnly){
            return podcastGateway.getAll().getPage(request.with(filter = filterRequest))
                    .map { it.toSearchDisplayableItem() }
        }
        return gateway.getAll().getPage(request.with(filter = filterRequest))
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
        dataSourceProvider: Provider<SearchPlaylistsDataSource>
) : BaseDataSourceFactory<DisplayableItem, SearchPlaylistsDataSource>(dataSourceProvider) {

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