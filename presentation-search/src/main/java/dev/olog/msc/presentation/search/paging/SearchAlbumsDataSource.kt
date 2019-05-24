package dev.olog.msc.presentation.search.paging

import androidx.paging.DataSource
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.SearchFilters
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.entity.data.request.with
import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.entity.track.Album
import dev.olog.msc.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.track.AlbumGateway
import dev.olog.msc.presentation.base.list.model.DisplayableItem
import dev.olog.msc.presentation.base.list.paging.BaseDataSource
import dev.olog.msc.presentation.base.list.paging.BaseDataSourceFactory
import dev.olog.msc.presentation.search.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class SearchAlbumsDataSource @Inject constructor(
        private val gateway: AlbumGateway,
        private val podcastGateway: PodcastAlbumGateway,
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

    private val filterRequest by lazy {
        Filter(
                filterBy,
                arrayOf(Filter.By.TITLE, Filter.By.ARTIST),
                Filter.BehaviorOnEmpty.NONE
        )
    }

    override fun getMainDataSize(): Int {
        if (podcastsOnly){
            return podcastGateway.getAll().getCount(filterRequest)
        }
        return gateway.getAll().getCount(filterRequest)
    }

    override fun getHeaders(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun loadInternal(request: Request): List<DisplayableItem> {
        if (podcastsOnly){
            return podcastGateway.getAll().getPage(request.with(filter = filterRequest))
                    .map { it.toSearchDisplayableItem() }
        }
        return gateway.getAll().getPage(request.with(filter = filterRequest))
                .map { it.toSearchDisplayableItem() }
    }


    private fun Album.toSearchDisplayableItem(): DisplayableItem {
        return DisplayableItem(
                R.layout.item_search_album,
                MediaId.albumId(id),
                title,
                artist
        )
    }

    private fun PodcastAlbum.toSearchDisplayableItem(): DisplayableItem {
        return DisplayableItem(
                R.layout.item_search_album,
                MediaId.podcastAlbumId(id),
                title,
                artist
        )
    }

}

internal class SearchAlbumsDataSourceFactory @Inject constructor(
        dataSourceProvider: Provider<SearchAlbumsDataSource>
) : BaseDataSourceFactory<DisplayableItem, SearchAlbumsDataSource>(dataSourceProvider) {

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