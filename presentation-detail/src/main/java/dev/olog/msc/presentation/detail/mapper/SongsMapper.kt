package dev.olog.msc.presentation.detail.mapper

import android.content.res.Resources
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.sort.SortType
import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.detail.R
import dev.olog.msc.shared.utils.TextUtils

internal fun Artist.toRelatedArtist(resources: Resources): DisplayableItem {
    val songs = DisplayableItem.handleSongListSize(resources, songs)
    var albums = DisplayableItem.handleAlbumListSize(resources, albums)
    if (albums.isNotBlank()) albums += TextUtils.MIDDLE_DOT_SPACED

    return DisplayableItem(
        R.layout.item_detail_related_artist,
        MediaId.artistId(this.id),
        this.name,
        albums + songs
    )
}

internal fun Song.toDetailDisplayableItem(parentId: MediaId, sortType: SortType): DisplayableItem {
    val viewType = when {
        parentId.isAlbum || parentId.isPodcastAlbum -> R.layout.item_detail_song_with_track
        (parentId.isPlaylist || parentId.isPodcastPlaylist) && sortType == SortType.CUSTOM -> {
            val playlistId = parentId.categoryValue.toLong()
            if (PlaylistGateway.isAutoPlaylist(playlistId) || PodcastPlaylistGateway.isPodcastAutoPlaylist(playlistId)) {
                R.layout.item_detail_song
            } else R.layout.item_detail_song_with_drag_handle
        }
        parentId.isFolder && sortType == SortType.TRACK_NUMBER -> R.layout.item_detail_song_with_track_and_image
        else -> R.layout.item_detail_song
    }

    val subtitle = when {
        parentId.isArtist || parentId.isPodcastArtist -> this.album
        else -> this.artist
    }

    val track = when {
        parentId.isPlaylist || parentId.isPodcastPlaylist -> this.trackNumber.toString()
        this.trackNumber == 0 -> "-"
        else -> this.trackNumber.toString()
    }

    return DisplayableItem(
        viewType,
        MediaId.playableItem(parentId, id),
        this.title,
        subtitle,
        true,
        track
    )
}

internal fun Song.toMostPlayedDetailDisplayableItem(parentId: MediaId): DisplayableItem {
    return DisplayableItem(
        R.layout.item_detail_song_most_played,
        MediaId.playableItem(parentId, id),
        this.title,
        this.artist,
        true
    )
}

internal fun Song.toRecentDetailDisplayableItem(parentId: MediaId): DisplayableItem {
    return DisplayableItem(
        R.layout.item_detail_song_recent,
        MediaId.playableItem(parentId, id),
        this.title,
        this.artist,
        true
    )
}
