package dev.olog.msc.presentation.tabs.domain

import dev.olog.msc.core.entity.sort.SortArranging
import dev.olog.msc.core.entity.sort.SortType
import dev.olog.msc.core.entity.track.Album
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.interactor.all.GetAllAlbumsUseCase
import dev.olog.msc.core.interactor.base.ObservableUseCase
import dev.olog.msc.shared.collator
import dev.olog.msc.shared.extensions.safeCompare
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

class GetAllAlbumsSortedUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val getAllUseCase: GetAllAlbumsUseCase,
        private val appPrefsGateway: AppPreferencesGateway

) : ObservableUseCase<List<Album>>(schedulers){

    override fun buildUseCaseObservable(): Observable<List<Album>> {
        return Observables.combineLatest(
                getAllUseCase.execute(),
                appPrefsGateway.observeAllAlbumsSortOrder()
        ) { tracks, order ->
            val (sort, arranging) = order

            if (arranging == SortArranging.ASCENDING){
                tracks.sortedWith(getAscendingComparator(sort))
            } else {
                tracks.sortedWith(getDescendingComparator(sort))
            }
        }
    }

    private fun getAscendingComparator(sortType: SortType): Comparator<Album> {
        return when (sortType){
            SortType.TITLE -> Comparator { o1, o2 -> collator.safeCompare(o1.title, o2.title) }
            SortType.ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o1.artist, o2.artist) }
            SortType.ALBUM_ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o1.albumArtist, o2.albumArtist) }
            else -> throw IllegalStateException("can't sort all albums, invalid sort type $sortType")
        }
    }

    private fun getDescendingComparator(sortType: SortType): Comparator<Album> {
        return when (sortType){
            SortType.TITLE -> Comparator { o1, o2 -> collator.safeCompare(o2.title, o1.title) }
            SortType.ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o2.artist, o1.artist) }
            SortType.ALBUM_ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o2.albumArtist, o1.albumArtist) }
            else -> throw IllegalStateException("can't sort all albums, invalid sort type $sortType")
        }
    }

}