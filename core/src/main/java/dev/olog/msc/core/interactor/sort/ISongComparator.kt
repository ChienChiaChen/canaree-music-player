package dev.olog.msc.core.interactor.sort

import dev.olog.msc.core.entity.sort.SortType
import dev.olog.msc.core.entity.track.Song

interface ISongComparator {
    fun getAscending(sortType: SortType): Comparator<Song>
    fun getDescending(sortType: SortType): Comparator<Song>
}