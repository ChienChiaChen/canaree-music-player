package dev.olog.msc.presentation.detail.mapper

import android.content.res.Resources
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.entity.podcast.PodcastArtist
import dev.olog.msc.core.entity.podcast.PodcastPlaylist
import dev.olog.msc.core.entity.track.*
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.detail.R
import dev.olog.msc.shared.TrackUtils
import dev.olog.msc.shared.utils.TextUtils

internal fun Folder.toHeaderItem(resources: Resources): DisplayableItem {

    return DisplayableItem(
        R.layout.item_detail_item_image,
        MediaId.folderId(path),
        title,
        subtitle = resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase(),
        image = image
    )

}

internal fun Playlist.toHeaderItem(resources: Resources): DisplayableItem {
    val listSize = if (this.size == -1) {
        ""
    } else {
        resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
    }

    return DisplayableItem(
        R.layout.item_detail_item_image,
        MediaId.playlistId(this.id),
        title,
        listSize,
        image = image
    )

}

internal fun Album.toHeaderItem(): DisplayableItem {

    return DisplayableItem(
        R.layout.item_detail_item_image,
        MediaId.albumId(this.id),
        title,
        TrackUtils.adjustArtist(this.artist),
        image = image
    )
}

internal fun Artist.toHeaderItem(resources: Resources): DisplayableItem {
    val songs = resources.getQuantityString(R.plurals.common_plurals_song, this.songs, this.songs)
    val albums = if (this.albums == 0) "" else {
        "${resources.getQuantityString(
            R.plurals.common_plurals_album,
            this.albums,
            this.albums
        )}${TextUtils.MIDDLE_DOT_SPACED}"
    }

    return DisplayableItem(
        R.layout.item_detail_item_image,
        MediaId.artistId(this.id),
        name,
        "$albums$songs".toLowerCase(),
        image = image
    )
}

internal fun Genre.toHeaderItem(resources: Resources): DisplayableItem {

    return DisplayableItem(
        R.layout.item_detail_item_image,
        MediaId.genreId(this.id),
        name,
        resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase(),
        image = image
    )
}


internal fun PodcastPlaylist.toHeaderItem(resources: Resources): DisplayableItem {
    val listSize = if (this.size == -1) {
        ""
    } else {
        resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
    }

    return DisplayableItem(
        R.layout.item_detail_item_image,
        MediaId.podcastPlaylistId(this.id),
        title,
        listSize,
        image = image
    )

}

internal fun PodcastAlbum.toHeaderItem(): DisplayableItem {

    return DisplayableItem(
        R.layout.item_detail_item_image,
        MediaId.podcastAlbumId(this.id),
        title,
        TrackUtils.adjustArtist(this.artist),
        image = image
    )
}

internal fun PodcastArtist.toHeaderItem(resources: Resources): DisplayableItem {
    val songs = resources.getQuantityString(R.plurals.common_plurals_song, this.songs, this.songs)
    val albums = if (this.albums == 0) "" else {
        "${resources.getQuantityString(
            R.plurals.common_plurals_album,
            this.albums,
            this.albums
        )}${TextUtils.MIDDLE_DOT_SPACED}"
    }

    return DisplayableItem(
        R.layout.item_detail_item_image,
        MediaId.podcastArtistId(this.id),
        name,
        "$albums$songs".toLowerCase(),
        image = image
    )
}