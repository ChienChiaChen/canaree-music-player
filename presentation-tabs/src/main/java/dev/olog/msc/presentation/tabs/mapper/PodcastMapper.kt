@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.msc.presentation.tabs.mapper

import android.content.res.Resources
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.entity.podcast.PodcastArtist
import dev.olog.msc.core.entity.podcast.PodcastPlaylist
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.tabs.R
import dev.olog.msc.shared.utils.TextUtils
import java.util.concurrent.TimeUnit

internal inline fun PodcastPlaylist.toTabDisplayableItem(resources: Resources): DisplayableItem {

    val size = DisplayableItem.handleSongListSize(resources, size)

    return DisplayableItem(
        R.layout.item_tab_album,
        MediaId.podcastPlaylistId(id),
        title,
        size,
        this.image
    )
}


internal inline fun PodcastPlaylist.toAutoPlaylist(): DisplayableItem {

    return DisplayableItem(
        R.layout.item_tab_auto_playlist,
        MediaId.podcastPlaylistId(id),
        title,
        "",
        this.image
    )
}

internal inline fun Podcast.toTabDisplayableItem(resources: Resources): DisplayableItem {
    val artist = this.artist

    val duration = resources.getString(R.string.tab_podcast_duration, TimeUnit.MILLISECONDS.toMinutes(this.duration))

    return DisplayableItem(
        R.layout.item_tab_podcast,
        MediaId.podcastId(this.id),
        title,
        artist,
        image,
        trackNumber = duration,
        isPlayable = true
    )
}

internal inline fun PodcastArtist.toTabDisplayableItem(resources: Resources): DisplayableItem {
    val songs = DisplayableItem.handleSongListSize(resources, songs)
    var albums = DisplayableItem.handleAlbumListSize(resources, albums)
    if (albums.isNotBlank()) albums+= TextUtils.MIDDLE_DOT_SPACED

    return DisplayableItem(
        R.layout.item_tab_artist,
        MediaId.podcastArtistId(id),
        name,
        albums + songs,
        this.image
    )
}


internal inline fun PodcastAlbum.toTabDisplayableItem(): DisplayableItem {
    return DisplayableItem(
        R.layout.item_tab_album,
        MediaId.podcastAlbumId(id),
        title,
        artist,
        image
    )
}

internal inline fun PodcastAlbum.toTabLastPlayedDisplayableItem(): DisplayableItem {
    return DisplayableItem(
        R.layout.item_tab_album_last_played,
        MediaId.podcastAlbumId(id),
        title,
        artist,
        image
    )
}

internal inline fun PodcastArtist.toTabLastPlayedDisplayableItem(resources: Resources): DisplayableItem {
    val songs = DisplayableItem.handleSongListSize(resources, songs)
    var albums = DisplayableItem.handleAlbumListSize(resources, albums)
    if (albums.isNotBlank()) albums+= TextUtils.MIDDLE_DOT_SPACED

    return DisplayableItem(
        R.layout.item_tab_artist_last_played,
        MediaId.podcastArtistId(id),
        name,
        albums + songs,
        this.image
    )
}