package dev.olog.msc.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.sort.SortArranging
import dev.olog.msc.core.entity.sort.SortType
import dev.olog.msc.core.interactor.sort.*
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.detail.domain.GetDetailSortDataUseCase
import dev.olog.msc.presentation.detail.paging.*
import dev.olog.msc.presentation.detail.sort.DetailSort
import dev.olog.msc.shared.extensions.debounceFirst
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class DetailFragmentViewModel @Inject constructor(
    val mediaId: MediaId,
    detailDataSource: DetailDataSourceFactory,
    siblingsDataSource: SiblingsDataSourceFactory,
    mostPlayedDataSource: MostPlayedDataSourceFactory,
    recentlyAddedDataSource: RecentlyAddedDataSourceFactory,
    relatedArtistsSource: RelatedArtistsSourceFactory,
    private val presenter: DetailFragmentPresenter,
    private val setSortOrderUseCase: SetSortOrderUseCase,
    private val observeSortOrderUseCase: GetSortOrderUseCase,
    private val setSortArrangingUseCase: SetSortArrangingUseCase,
    private val getSortArrangingUseCase: GetSortArrangingUseCase,
    private val getDetailSortDataUseCase: GetDetailSortDataUseCase

) : ViewModel() {

    companion object {
        const val NESTED_SPAN_COUNT = 4
        const val RECENTLY_ADDED_VISIBLE_PAGES = NESTED_SPAN_COUNT * 4
        const val RELATED_ARTISTS_TO_SEE = 10
    }

    private val subscriptions = CompositeDisposable()

    private val filterPublisher = BehaviorSubject.createDefault("")

    val data : LiveData<PagedList<DisplayableItem>>
    val siblings: LiveData<PagedList<DisplayableItem>>
    val mostPlayed: LiveData<PagedList<DisplayableItem>>
    val recentlyAdded: LiveData<PagedList<DisplayableItem>>
    val relatedArtists: LiveData<PagedList<DisplayableItem>>

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
    }

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
        subscriptions.clear()
    }

    fun detailSortDataUseCase(mediaId: MediaId, action: (DetailSort) -> Unit){
        getDetailSortDataUseCase.execute(mediaId)
                .subscribe(action, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

    fun observeSortOrder(action: (SortType) -> Unit) {
        observeSortOrderUseCase.execute(mediaId)
                .firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ action(it) }, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

    fun updateSortOrder(sortType: SortType){
        setSortOrderUseCase.execute(SetSortOrderRequestModel(mediaId, sortType))
                .subscribe({ }, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

    fun toggleSortArranging(){
        observeSortOrderUseCase.execute(mediaId)
                .firstOrError()
                .filter { it != SortType.CUSTOM }
                .flatMapCompletable { setSortArrangingUseCase.execute() }
                .subscribe({ }, Throwable::printStackTrace)
                .addTo(subscriptions)

    }

    fun moveItemInPlaylist(from: Int, to: Int){
        presenter.moveInPlaylist(from, to)
    }

    fun removeFromPlaylist(item: DisplayableItem) {
        presenter.removeFromPlaylist(item)
                .subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

    fun observeSorting(): Observable<Pair<SortType, SortArranging>>{
        return Observables.combineLatest(
                observeSortOrderUseCase.execute(mediaId),
                getSortArrangingUseCase.execute(),
                { sort, arranging -> Pair(sort, arranging) }
        )
    }

    fun showSortByTutorialIfNeverShown(): Completable {
        return presenter.showSortByTutorialIfNeverShown()
    }

}