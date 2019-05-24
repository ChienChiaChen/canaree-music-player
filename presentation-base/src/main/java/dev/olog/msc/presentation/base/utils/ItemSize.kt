package dev.olog.msc.presentation.base.utils

import android.content.res.Resources
import dev.olog.msc.presentation.base.R

fun handleSongListSize(resources: Resources, size: Int): String {
    if (size <= 0) {
        return ""
    }
    return resources.getQuantityString(R.plurals.common_plurals_song, size, size).toLowerCase()
}

fun handleAlbumListSize(resources: Resources, size: Int): String {
    if (size <= 0) {
        return ""
    }
    return resources.getQuantityString(R.plurals.common_plurals_album, size, size).toLowerCase()
}