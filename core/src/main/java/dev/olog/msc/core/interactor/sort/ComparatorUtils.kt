package dev.olog.msc.core.interactor.sort

import dev.olog.msc.core.entity.track.Song

object ComparatorUtils {

    fun getAscendingTrackNumberComparator(): Comparator<Song> {
        return Comparator { o1: Song, o2: Song ->
            val tmp = o1.discNumber - o2.discNumber
            if (tmp == 0){
                o1.trackNumber - o2.trackNumber
            } else {
                tmp
            }
        }
    }

    fun getDescendingTrackNumberComparator(): Comparator<Song> {
        return Comparator { o1: Song, o2: Song ->
            val tmp = o2.discNumber - o1.discNumber
            if (tmp == 0){
                o2.trackNumber - o1.trackNumber
            } else {
                tmp
            }
        }
    }



}