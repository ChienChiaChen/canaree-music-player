package dev.olog.msc.musicservice

import dev.olog.msc.musicservice.model.MediaEntity

object ComparatorUtils {
    fun getMediaEntityAscendingTrackNumberComparator(): Comparator<MediaEntity> {
        return Comparator { o1: MediaEntity, o2: MediaEntity ->
            val tmp = o1.discNumber - o2.discNumber
            if (tmp == 0) {
                o1.trackNumber - o2.trackNumber
            } else {
                tmp
            }
        }
    }

    fun getMediaEntityDescendingTrackNumberComparator(): Comparator<MediaEntity> {
        return Comparator { o1: MediaEntity, o2: MediaEntity ->
            val tmp = o2.discNumber - o1.discNumber
            if (tmp == 0) {
                o2.trackNumber - o1.trackNumber
            } else {
                tmp
            }
        }
    }
}
