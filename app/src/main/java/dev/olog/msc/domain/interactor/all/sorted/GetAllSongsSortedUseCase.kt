package dev.olog.msc.domain.interactor.all.sorted

import dev.olog.msc.core.entity.sort.SortArranging
import dev.olog.msc.core.entity.sort.SortType
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.interactor.all.GetAllSongsUseCase
import dev.olog.msc.core.interactor.base.ObservableUseCase
import dev.olog.msc.shared.extensions.safeCompare
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import java.text.Collator
import javax.inject.Inject

class GetAllSongsSortedUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val getAllUseCase: GetAllSongsUseCase,
        private val appPrefsGateway: AppPreferencesGateway,
        private val collator: Collator


) : ObservableUseCase<List<Song>>(schedulers){

    override fun buildUseCaseObservable(): Observable<List<Song>> {
        return Observables.combineLatest(
                getAllUseCase.execute(),
                appPrefsGateway.observeAllTracksSortOrder()
            ) { tracks, order ->
                val (sort, arranging) = order

                if (arranging == SortArranging.ASCENDING){
                    tracks.sortedWith(getAscendingComparator(sort))
                } else {
                    tracks.sortedWith(getDescendingComparator(sort))
                }
            }
    }

    private fun getAscendingComparator(sortType: SortType): Comparator<Song> {
        return when (sortType){
            SortType.TITLE -> Comparator { o1, o2 -> collator.safeCompare(o1.title, o2.title) }
            SortType.ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o1.artist, o2.artist) }
            SortType.ALBUM_ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o1.albumArtist, o2.albumArtist) }
            SortType.ALBUM -> Comparator { o1, o2 -> collator.safeCompare(o1.album, o2.album) }
            SortType.DURATION -> compareBy { it.duration }
            SortType.RECENTLY_ADDED -> compareBy { it.dateAdded }
            else -> throw IllegalStateException("can't sort all tracks, invalid sort type $sortType")
        }
    }

    private fun getDescendingComparator(sortType: SortType): Comparator<Song> {
        return when (sortType){
            SortType.TITLE -> Comparator { o1, o2 -> collator.safeCompare(o2.title, o1.title) }
            SortType.ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o2.artist, o1.artist) }
            SortType.ALBUM_ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o2.albumArtist, o1.albumArtist) }
            SortType.ALBUM -> Comparator { o1, o2 -> collator.safeCompare(o2.album, o1.album) }
            SortType.DURATION -> compareByDescending { it.duration }
            SortType.RECENTLY_ADDED -> compareByDescending { it.dateAdded }
            else -> throw IllegalStateException("can't sort all tracks, invalid sort type $sortType")
        }
    }

}