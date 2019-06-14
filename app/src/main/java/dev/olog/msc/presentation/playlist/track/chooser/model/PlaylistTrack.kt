package dev.olog.msc.presentation.playlist.track.chooser.model

import dev.olog.msc.R
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.Podcast
import dev.olog.msc.core.entity.Song
import dev.olog.msc.presentation.model.DisplayableItem

data class PlaylistTrack (
        val id: Long,
        val artistId: Long,
        val albumId: Long,
        val title: String,
        val artist: String,
        val albumArtist: String,
        val album: String,
        val duration: Long,
        val dateAdded: Long,
        val path: String,
        val folder: String,
        val discNumber: Int,
        val trackNumber: Int,
        val isPodcast: Boolean) {

}

internal fun PlaylistTrack.toDisplayableItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_choose_track,
            if (this.isPodcast) MediaId.podcastId(this.id) else MediaId.songId(this.id),
            this.title,
            DisplayableItem.adjustArtist(this.artist),
            true
    )
}

internal fun Podcast.toPlaylistTrack(): PlaylistTrack {
    return PlaylistTrack(
            this.id,
            this.artistId,
            this.albumId,
            this.title,
            this.artist,
            this.albumArtist,
            this.album,
            this.duration,
            this.dateAdded,
            this.path,
            this.folder,
            this.discNumber,
            this.trackNumber,
            true
    )
}

internal fun Song.toPlaylistTrack(): PlaylistTrack {
    return PlaylistTrack(
            this.id,
            this.artistId,
            this.albumId,
            this.title,
            this.artist,
            this.albumArtist,
            this.album,
            this.duration,
            this.dateAdded,
            this.path,
            this.folder,
            this.discNumber,
            this.trackNumber,
            false
    )
}