package dev.olog.msc

import dev.olog.msc.core.entity.sort.SortType
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.interactor.sort.ComparatorUtils
import dev.olog.msc.core.interactor.sort.ISongComparator
import dev.olog.msc.shared.collator
import dev.olog.msc.shared.extensions.safeCompare
import javax.inject.Inject

class SongComparatorImpl @Inject constructor() : ISongComparator {
    override fun getAscending(sortType: SortType): Comparator<Song> {
        return when (sortType) {
            SortType.TITLE -> Comparator { o1, o2 -> collator.safeCompare(o1.title, o2.title) }
            SortType.ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o1.artist, o2.artist) }
            SortType.ALBUM -> Comparator { o1, o2 -> collator.safeCompare(o1.album, o2.album) }
            SortType.ALBUM_ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o1.albumArtist, o2.albumArtist) }
            SortType.DURATION -> compareBy { it.duration }
            SortType.RECENTLY_ADDED -> compareByDescending { it.dateAdded }
            SortType.TRACK_NUMBER -> ComparatorUtils.getAscendingTrackNumberComparator()
            SortType.CUSTOM -> compareBy { 0 }
        }
    }

    override fun getDescending(sortType: SortType): Comparator<Song> {
        return when (sortType) {
            SortType.TITLE -> Comparator { o1, o2 -> collator.safeCompare(o2.title, o1.title) }
            SortType.ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o2.artist, o1.artist) }
            SortType.ALBUM -> Comparator { o1, o2 -> collator.safeCompare(o2.album, o1.album) }
            SortType.ALBUM_ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o2.albumArtist, o1.albumArtist) }
            SortType.DURATION -> compareByDescending { it.duration }
            SortType.RECENTLY_ADDED -> compareBy { it.dateAdded }
            SortType.TRACK_NUMBER -> ComparatorUtils.getDescendingTrackNumberComparator()
            SortType.CUSTOM -> compareByDescending { 0 }
        }
    }
}