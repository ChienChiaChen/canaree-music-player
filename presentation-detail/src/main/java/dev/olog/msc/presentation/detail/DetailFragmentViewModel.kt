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
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.prefs.TutorialPreferenceGateway
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.core.interactor.sort.SetSortOrderRequestModel
import dev.olog.msc.core.interactor.sort.SetSortOrderUseCase
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.detail.domain.GetDetailSortDataUseCase
import dev.olog.msc.presentation.detail.domain.MoveItemInPlaylistUseCase
import dev.olog.msc.presentation.detail.domain.RemoveFromPlaylistUseCase
import dev.olog.msc.presentation.detail.paging.*
import dev.olog.msc.presentation.detail.sort.DetailSort
import dev.olog.msc.shared.extensions.debounceFirst
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class DetailFragmentViewModel @Inject constructor(
    val mediaId: MediaId,
    detailDataSource: DetailDataSourceFactory,
    siblingsDataSource: SiblingsDataSourceFactory,
    mostPlayedDataSource: MostPlayedDataSourceFactory,
    recentlyAddedDataSource: RecentlyAddedDataSourceFactory,
    relatedArtistsSource: RelatedArtistsSourceFactory,
    private val setSortOrderUseCase: SetSortOrderUseCase,
    private val prefsGateway: AppPreferencesGateway,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway,
    private val getDetailSortDataUseCase: GetDetailSortDataUseCase,
    private val removeFromPlaylistUseCase: RemoveFromPlaylistUseCase,
    private val moveItemInPlaylistUseCase: MoveItemInPlaylistUseCase

) : ViewModel() {

    companion object {
        const val NESTED_SPAN_COUNT = 4
        const val RECENTLY_ADDED_VISIBLE_PAGES = NESTED_SPAN_COUNT * 4
        const val RELATED_ARTISTS_TO_SEE = 10
    }

    private val filterPublisher = BehaviorSubject.createDefault("")

    val data : LiveData<PagedList<DisplayableItem>>
    val siblings: LiveData<PagedList<DisplayableItem>>
    val mostPlayed: LiveData<PagedList<DisplayableItem>>
    val recentlyAdded: LiveData<PagedList<DisplayableItem>>
    val relatedArtists: LiveData<PagedList<DisplayableItem>>

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

        viewModelScope.launch {
            getDetailSortDataUseCase.execute(mediaId)
                .collect { sortingLiveData.postValue(it) }
        }
    }

    fun observeSorting(): LiveData<DetailSort> = sortingLiveData

    fun updateFilter(filter: String){
        if (filter.isEmpty() || filter.length >= 2){
            filterPublisher.onNext(filter.toLowerCase())
        }
    }


    private fun filterSongs(songObservable: Observable<List<DisplayableItem>>): Observable<List<DisplayableItem>>{
        return Observables.combineLatest(
                songObservable.debounceFirst(50, TimeUnit.MILLISECONDS).distinctUntilChanged(),
                filterPublisher.debounceFirst().distinctUntilChanged()
        ) { songs, filter ->
            if (filter.isBlank()){
                songs
            } else {
                songs.filter {
                    it.title.toLowerCase().contains(filter) || it.subtitle?.toLowerCase()?.contains(filter) == true
                }
            }
        }.distinctUntilChanged()
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

    fun getDetailSort(action: (DetailSort) -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        val sortData = getDetailSortDataUseCase.execute(mediaId).single()
        withContext(Dispatchers.Main){
            action(sortData)
        }
    }

    fun updateSortOrder(sortType: SortType) = viewModelScope.launch(Dispatchers.IO) {
        setSortOrderUseCase.execute(SetSortOrderRequestModel(mediaId, sortType))
    }

    fun toggleSortArranging() = viewModelScope.launch(Dispatchers.IO) {
        if (getDetailSortDataUseCase.execute(mediaId).single().sortType != SortType.CUSTOM){
            prefsGateway.toggleSortArranging()
        }

    }

    fun moveItemInPlaylist(from: Int, to: Int) = viewModelScope.launch{
        mediaId.assertPlaylist()
        val playlistId = mediaId.resolveId
        moveItemInPlaylistUseCase.execute(
            MoveItemInPlaylistUseCase.Input(playlistId, from, to,
                if (mediaId.isPodcastPlaylist) PlaylistType.PODCAST else PlaylistType.TRACK
            ))
    }

    fun removeFromPlaylist(item: DisplayableItem) = viewModelScope.launch {
        mediaId.assertPlaylist()
        val playlistId = mediaId.resolveId
        val playlistType = if (item.mediaId.isPodcast) PlaylistType.PODCAST else PlaylistType.TRACK
        if (playlistId == PlaylistGateway.FAVORITE_LIST_ID){
            // favorites use songId instead of idInPlaylist
            removeFromPlaylistUseCase.execute(RemoveFromPlaylistUseCase.Input(playlistId, item.mediaId.leaf!!, playlistType))
        } else {
            removeFromPlaylistUseCase.execute(RemoveFromPlaylistUseCase.Input(playlistId, item.trackNumber.toLong(), playlistType))
        }
    }

    fun canShowSortByTutorial(onCanShow: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        if (tutorialPreferenceUseCase.canShowSortByTutorial()){
            viewModelScope.launch(Dispatchers.Main) {
                onCanShow()
            }
        }
    }

}