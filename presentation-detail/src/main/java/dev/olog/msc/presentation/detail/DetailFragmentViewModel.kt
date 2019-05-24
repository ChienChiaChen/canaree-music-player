package dev.olog.msc.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.PlaylistType
import dev.olog.msc.core.entity.sort.SortType
import dev.olog.msc.core.gateway.prefs.SortPreferencesGateway
import dev.olog.msc.core.gateway.prefs.TutorialPreferenceGateway
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.core.interactor.ObserveItemTitleUseCase
import dev.olog.msc.core.interactor.sort.SetSortOrderRequestModel
import dev.olog.msc.core.interactor.sort.SetSortOrderUseCase
import dev.olog.msc.presentation.base.list.model.DisplayableItem
import dev.olog.msc.presentation.detail.domain.GetDetailSortDataUseCase
import dev.olog.msc.presentation.detail.domain.MoveItemInPlaylistUseCase
import dev.olog.msc.presentation.detail.domain.ObserveDetailSortDataUseCase
import dev.olog.msc.presentation.detail.domain.RemoveFromPlaylistUseCase
import dev.olog.msc.presentation.detail.domain.RemoveFromPlaylistUseCase.Input
import dev.olog.msc.presentation.detail.paging.*
import dev.olog.msc.presentation.detail.sort.DetailSort
import dev.olog.msc.shared.ui.extensions.liveDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class DetailFragmentViewModel @Inject constructor(
    val mediaId: MediaId,
    private val detailDataSource: DetailDataSourceFactory,
    private val siblingsDataSource: SiblingsDataSourceFactory,
    private val mostPlayedDataSource: MostPlayedDataSourceFactory,
    private val recentlyAddedDataSource: RecentlyAddedDataSourceFactory,
    private val relatedArtistsSource: RelatedArtistsSourceFactory,
    private val setSortOrderUseCase: SetSortOrderUseCase,
    private val prefsGateway: SortPreferencesGateway,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway,
    private val observeDetailSortDataUseCase: ObserveDetailSortDataUseCase,
    private val getDetailSortDataUseCase: GetDetailSortDataUseCase,
    private val removeFromPlaylistUseCase: RemoveFromPlaylistUseCase,
    private val moveItemInPlaylistUseCase: MoveItemInPlaylistUseCase,
    private val observeItemTitleUseCase: ObserveItemTitleUseCase

) : ViewModel() {

    companion object {
        const val NESTED_SPAN_COUNT = 4
        const val RECENTLY_ADDED_VISIBLE_PAGES = NESTED_SPAN_COUNT * 4
        const val RELATED_ARTISTS_TO_SEE = 10
    }

    val data: LiveData<PagedList<DisplayableItem>>
    val siblings: LiveData<PagedList<DisplayableItem>>
    val mostPlayed: LiveData<PagedList<DisplayableItem>>
    val recentlyAdded: LiveData<PagedList<DisplayableItem>>
    val relatedArtists: LiveData<PagedList<DisplayableItem>>
    private val titleLiveData = liveDataOf<String>()

    private val sortingLiveData = MutableLiveData<DetailSort>()

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
        data = LivePagedListBuilder(detailDataSource, config).build()
        siblings = LivePagedListBuilder(siblingsDataSource, config).build()
        mostPlayed = LivePagedListBuilder(mostPlayedDataSource, miniConfig).build()
        recentlyAdded = LivePagedListBuilder(recentlyAddedDataSource, miniConfig).build()
        relatedArtists = LivePagedListBuilder(relatedArtistsSource, miniConfig).build()

        viewModelScope.launch(Dispatchers.Default) {
            observeDetailSortDataUseCase.execute(mediaId)
                .collect {
                    sortingLiveData.postValue(it)
                }
        }
        viewModelScope.launch(Dispatchers.Default) {
            observeItemTitleUseCase.execute(mediaId)
                    .distinctUntilChanged()
                    .collect { titleLiveData.postValue(it) }
        }
    }

    fun observeSorting(): LiveData<DetailSort> = sortingLiveData
    fun observeTitle() : LiveData<String> = titleLiveData

    fun updateFilter(filter: String) {
        detailDataSource.updateFilterBy(filter)
        relatedArtistsSource.updateFilterBy(filter)
        siblingsDataSource.updateFilterBy(filter)
    }

    override fun onCleared() {
        viewModelScope.cancel()
        detailDataSource.onDetach()
        siblingsDataSource.onDetach()
        mostPlayedDataSource.onDetach()
        recentlyAddedDataSource.onDetach()
        relatedArtistsSource.onDetach()
    }

    fun getDetailSort(action: (DetailSort) -> Unit) = viewModelScope.launch(Dispatchers.Default) {
        val sortData = getDetailSortDataUseCase.execute(mediaId)
        withContext(Dispatchers.Main) {
            action(sortData)
        }
    }

    fun updateSortOrder(sortType: SortType) = viewModelScope.launch(Dispatchers.Default) {
        setSortOrderUseCase.execute(SetSortOrderRequestModel(mediaId, sortType))
    }

    fun toggleSortArranging() = viewModelScope.launch(Dispatchers.Default) {
        if (getDetailSortDataUseCase.execute(mediaId).sortType != SortType.CUSTOM) {
            prefsGateway.toggleSortArranging()
        }
    }

    fun moveItemInPlaylist(headersCount: Int, itemToMove: List<Pair<Int, Int>>) =
        viewModelScope.launch(Dispatchers.Default) {
            mediaId.assertPlaylist()
            val playlistId = mediaId.resolveId
            val type = if (mediaId.isPodcastPlaylist) PlaylistType.PODCAST else PlaylistType.TRACK
            for ((from, to) in itemToMove) {
                moveItemInPlaylistUseCase.execute(
                    MoveItemInPlaylistUseCase.Input(
                        playlistId,
                        from - headersCount,
                        to - headersCount,
                        type
                    )
                )
            }
            withContext(Dispatchers.Main) {
                detailDataSource.invalidate()
            }
        }

    fun removeFromPlaylist(item: DisplayableItem) = viewModelScope.launch(Dispatchers.Default) {
        mediaId.assertPlaylist()
        val playlistId = mediaId.resolveId
        val playlistType = if (item.mediaId.isPodcast) PlaylistType.PODCAST else PlaylistType.TRACK
        if (playlistId == PlaylistGateway.FAVORITE_LIST_ID) {
            // favorites use songId instead of idInPlaylist
            removeFromPlaylistUseCase.execute(Input(playlistId, item.mediaId.leaf!!, playlistType))
        } else {
            removeFromPlaylistUseCase.execute(Input(playlistId, item.trackNumber.toLong(), playlistType))
        }
        withContext(Dispatchers.Main) {
            detailDataSource.invalidate()
        }
    }

    fun canShowSortByTutorial(onCanShow: () -> Unit) = viewModelScope.launch(Dispatchers.Default) {
        if (tutorialPreferenceUseCase.canShowSortByTutorial()) {
            viewModelScope.launch(Dispatchers.Main) {
                onCanShow()
            }
        }
    }

}