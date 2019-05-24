package dev.olog.msc.presentation.detail.mapper

import android.content.res.Resources
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.entity.podcast.PodcastPlaylist
import dev.olog.msc.core.entity.track.Album
import dev.olog.msc.core.entity.track.Folder
import dev.olog.msc.core.entity.track.Genre
import dev.olog.msc.core.entity.track.Playlist
import dev.olog.msc.presentation.base.list.model.DisplayableItem
import dev.olog.msc.presentation.detail.R

internal fun Folder.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
        R.layout.item_detail_album,
        MediaId.folderId(path),
        title,
        resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
    )
}

internal fun Playlist.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
        R.layout.item_detail_album,
        MediaId.playlistId(id),
        title,
        resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
    )
}

internal fun Album.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
        R.layout.item_detail_album,
        MediaId.albumId(id),
        title,
        resources.getQuantityString(R.plurals.common_plurals_song, this.songs, this.songs).toLowerCase()
    )
}

internal fun Genre.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
        R.layout.item_detail_album,
        MediaId.genreId(id),
        name,
        resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
    )
}

internal fun PodcastPlaylist.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
        R.layout.item_detail_album,
        MediaId.podcastPlaylistId(id),
        title,
        resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
    )
}

internal fun PodcastAlbum.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
        R.layout.item_detail_album,
        MediaId.podcastAlbumId(id),
        title,
        resources.getQuantityString(R.plurals.common_plurals_song, this.songs, this.songs).toLowerCase()
    )
}