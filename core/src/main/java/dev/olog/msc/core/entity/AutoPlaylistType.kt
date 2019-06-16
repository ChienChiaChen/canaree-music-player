package dev.olog.msc.core.entity

import dev.olog.msc.core.utils.negative

enum class AutoPlaylistType {
    LAST_ADDED,
    FAVORITE,
    HISTORY;

    companion object {
        fun isAutoPlaylist(id: Long): Boolean {
            return values()
                .map { it.id }
                .any { it == id }
        }
    }

}

// makes the hashcode negative to prevent having conflict with real playlist id that are
// strictly positive
val AutoPlaylistType.id: Long
    get() = this.hashCode().toLong().negative()