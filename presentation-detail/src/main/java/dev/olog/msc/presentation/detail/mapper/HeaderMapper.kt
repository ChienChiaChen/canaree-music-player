package dev.olog.msc.presentation.detail.mapper

import android.content.res.Resources
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.entity.podcast.PodcastArtist
import dev.olog.msc.core.entity.podcast.PodcastPlaylist
import dev.olog.msc.core.entity.track.*
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.detail.R
import dev.olog.msc.shared.utils.TextUtils

internal fun Folder.toHeaderItem(resources: Resources, listSize: Int): DisplayableItem {

    return DisplayableItem(
        R.layout.item_detail_item_image,
        MediaId.folderId(path),
        title,
        subtitle = resources.getQuantityString(R.plurals.common_plurals_song, listSize, listSize).toLowerCase()
    )

}

internal fun Playlist.toHeaderItem(resources: Resources, listSize: Int): DisplayableItem {
    val finalListSize = if (listSize == -1) {
        ""
    } else {
        resources.getQuantityString(R.plurals.common_plurals_song, listSize, listSize).toLowerCase()
    }

    return DisplayableItem(
        R.layout.item_detail_item_image,
        MediaId.playlistId(this.id),
        title,
        finalListSize
    )

}

internal fun Album.toHeaderItem(): DisplayableItem {

    return DisplayableItem(
        R.layout.item_detail_item_image,
        MediaId.albumId(this.id),
        title,
        this.artist
    )
}

internal fun Artist.toHeaderItem(resources: Resources, songListSize: Int, albumListSize: Int): DisplayableItem {
    val songs = resources.getQuantityString(R.plurals.common_plurals_song, songListSize, songListSize)
    val albums = if (albumListSize == 0) "" else {
        "${resources.getQuantityString(
            R.plurals.common_plurals_album,
            albumListSize,
            albumListSize
        )}${TextUtils.MIDDLE_DOT_SPACED}"
    }

    return DisplayableItem(
        R.layout.item_detail_item_image,
        MediaId.artistId(this.id),
        name,
        "$albums$songs".toLowerCase()
    )
}

internal fun Genre.toHeaderItem(resources: Resources, listSize: Int): DisplayableItem {

    return DisplayableItem(
        R.layout.item_detail_item_image,
        MediaId.genreId(this.id),
        name,
        resources.getQuantityString(R.plurals.common_plurals_song, listSize, listSize).toLowerCase()
    )
}


internal fun PodcastPlaylist.toHeaderItem(resources: Resources, listSize: Int): DisplayableItem {
    val finalListSize = if (listSize == -1) {
        ""
    } else {
        resources.getQuantityString(R.plurals.common_plurals_song, listSize, listSize).toLowerCase()
    }

    return DisplayableItem(
        R.layout.item_detail_item_image,
        MediaId.podcastPlaylistId(this.id),
        title,
        finalListSize
    )

}

internal fun PodcastAlbum.toHeaderItem(): DisplayableItem {

    return DisplayableItem(
        R.layout.item_detail_item_image,
        MediaId.podcastAlbumId(this.id),
        title,
        this.artist
    )
}

internal fun PodcastArtist.toHeaderItem(resources: Resources, songListSize: Int, albumListSize: Int): DisplayableItem {
    val songs = resources.getQuantityString(R.plurals.common_plurals_song, songListSize, songListSize)
    val albums = if (albumListSize == 0) "" else {
        "${resources.getQuantityString(
            R.plurals.common_plurals_album,
            albumListSize,
            albumListSize
        )}${TextUtils.MIDDLE_DOT_SPACED}"
    }

    return DisplayableItem(
        R.layout.item_detail_item_image,
        MediaId.podcastArtistId(this.id),
        name,
        "$albums$songs".toLowerCase()
    )
}