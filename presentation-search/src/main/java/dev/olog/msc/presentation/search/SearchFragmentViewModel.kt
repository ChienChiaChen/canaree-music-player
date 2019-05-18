package dev.olog.msc.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dev.olog.msc.core.MediaId
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.search.domain.ClearRecentSearchesUseCase
import dev.olog.msc.presentation.search.domain.DeleteRecentSearchUseCase
import dev.olog.msc.presentation.search.domain.InsertRecentSearchUseCase
import dev.olog.msc.presentation.search.paging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class SearchFragmentViewModel @Inject constructor(
        private val insertRecentUse: InsertRecentSearchUseCase,
        private val deleteRecentSearchUseCase: DeleteRecentSearchUseCase,
        private val clearRecentSearchesUseCase: ClearRecentSearchesUseCase,
        private val searchDataSource: SearchDataSourceFactory,
        private val searchArtistsDataSource: SearchArtistsDataSourceFactory,
        private val searchAlbumsDataSource: SearchAlbumsDataSourceFactory,
        private val searchFoldersDataSource: SearchFoldersDataSourceFactory,
        private val searchPlaylistsDataSource: SearchPlaylistsDataSourceFactory,
        private val searchGenresDataSource: SearchGenresDataSourceFactory

) : ViewModel() {

    val data : LiveData<PagedList<DisplayableItem>>
    val albumsData : LiveData<PagedList<DisplayableItem>>
    val artistsData : LiveData<PagedList<DisplayableItem>>
    val foldersData : LiveData<PagedList<DisplayableItem>>
    val playlistData : LiveData<PagedList<DisplayableItem>>
    val genreData : LiveData<PagedList<DisplayableItem>>

    init {
        val config = PagedList.Config.Builder()
            .setPageSize(20)
            .setInitialLoadSizeHint(20)
            .setEnablePlaceholders(true)
            .build()
        val miniConfig = PagedList.Config.Builder()
            .setInitialLoadSizeHint(8)
            .setPageSize(4)
            .setEnablePlaceholders(true)
            .build()
        data = LivePagedListBuilder(searchDataSource, config).build()
        albumsData = LivePagedListBuilder(searchAlbumsDataSource, miniConfig).build()
        artistsData = LivePagedListBuilder(searchArtistsDataSource, miniConfig).build()
        foldersData = LivePagedListBuilder(searchFoldersDataSource, miniConfig).build()
        genreData = LivePagedListBuilder(searchGenresDataSource, miniConfig).build()
        playlistData = LivePagedListBuilder(searchPlaylistsDataSource, miniConfig).build()
    }

    fun updateFilter(filter: String){
        val trimmed = filter.trim()
        searchDataSource.updateFilterBy(trimmed)
        searchArtistsDataSource.updateFilterBy(trimmed)
        searchAlbumsDataSource.updateFilterBy(trimmed)
        searchFoldersDataSource.updateFilterBy(trimmed)
        searchPlaylistsDataSource.updateFilterBy(trimmed)
        searchGenresDataSource.updateFilterBy(trimmed)
    }

    private fun invalidateData(){
        searchDataSource.invalidate()
        searchArtistsDataSource.invalidate()
        searchAlbumsDataSource.invalidate()
        searchFoldersDataSource.invalidate()
        searchPlaylistsDataSource.invalidate()
        searchGenresDataSource.invalidate()
    }

    fun insertToRecent(mediaId: MediaId) = viewModelScope.launch(Dispatchers.Default){
        insertRecentUse.execute(mediaId)
    }

    fun deleteFromRecent(mediaId: MediaId)= viewModelScope.launch(Dispatchers.Default){
        deleteRecentSearchUseCase.execute(mediaId)
        withContext(Dispatchers.Main){
            invalidateData()
        }
    }

    fun clearRecentSearches() = viewModelScope.launch(Dispatchers.Default){
        clearRecentSearchesUseCase.execute()
        withContext(Dispatchers.Main){
            invalidateData()
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

}