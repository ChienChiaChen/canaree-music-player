@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.msc.presentation.tabs.mapper

import android.content.res.Resources
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.track.*
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.tabs.R
import dev.olog.msc.shared.TrackUtils
import dev.olog.msc.shared.utils.TextUtils

internal inline fun Folder.toTabDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
        R.layout.item_tab_album,
        MediaId.folderId(path),
        title,
        DisplayableItem.handleSongListSize(resources, size),
        this.image
    )
}

internal inline fun Playlist.toAutoPlaylist(): DisplayableItem {

    return DisplayableItem(
        R.layout.item_tab_auto_playlist,
        MediaId.playlistId(id),
        title,
        "",
        this.image
    )
}

internal inline fun Playlist.toTabDisplayableItem(resources: Resources): DisplayableItem {

    val size = DisplayableItem.handleSongListSize(resources, size)

    return DisplayableItem(
        R.layout.item_tab_album,
        MediaId.playlistId(id),
        title,
        size,
        this.image
    )
}

internal inline fun Song.toTabDisplayableItem(): DisplayableItem {
    val artist = TrackUtils.adjustArtist(this.artist)
    val album = TrackUtils.adjustAlbum(this.album)

    return DisplayableItem(
        R.layout.item_tab_song,
        MediaId.songId(this.id),
        title,
        "$artist${TextUtils.MIDDLE_DOT_SPACED}$album",
        image,
        true
    )
}


internal inline fun Album.toTabDisplayableItem(): DisplayableItem {
    return DisplayableItem(
        R.layout.item_tab_album,
        MediaId.albumId(id),
        title,
        TrackUtils.adjustArtist(artist),
        image
    )
}

internal inline fun Artist.toTabDisplayableItem(resources: Resources): DisplayableItem {
    val songs = DisplayableItem.handleSongListSize(resources, songs)
    var albums = DisplayableItem.handleAlbumListSize(resources, albums)
    if (albums.isNotBlank()) albums += TextUtils.MIDDLE_DOT_SPACED

    return DisplayableItem(
        R.layout.item_tab_artist,
        MediaId.artistId(id),
        name,
        albums + songs,
        this.image
    )
}


internal inline fun Genre.toTabDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
        R.layout.item_tab_album,
        MediaId.genreId(id),
        name,
        DisplayableItem.handleSongListSize(resources, size),
        this.image
    )
}

internal inline fun Album.toTabLastPlayedDisplayableItem(): DisplayableItem {
    return DisplayableItem(
        R.layout.item_tab_album_last_played,
        MediaId.albumId(id),
        title,
        TrackUtils.adjustArtist(artist),
        image
    )
}

internal inline fun Artist.toTabLastPlayedDisplayableItem(resources: Resources): DisplayableItem {
    val songs = DisplayableItem.handleSongListSize(resources, songs)
    var albums = DisplayableItem.handleAlbumListSize(resources, albums)
    if (albums.isNotBlank()) albums += TextUtils.MIDDLE_DOT_SPACED

    return DisplayableItem(
        R.layout.item_tab_artist_last_played,
        MediaId.artistId(id),
        name,
        albums + songs,
        this.image
    )
}