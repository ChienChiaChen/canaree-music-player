package dev.olog.msc.musicservice.model

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.PlayingQueueSong
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.track.Song

internal data class MediaEntity(
        val id: Long,
        val idInPlaylist: Int,
        val mediaId: MediaId,
        val artistId: Long,
        val albumId: Long,
        val title: String,
        val artist: String,
        val albumArtist: String,
        val album: String,
        val image: String,
        val duration: Long,
        val dateAdded: Long,
        val path: String,
        val discNumber: Int,
        val trackNumber: Int,
        val isPodcast: Boolean
)

internal fun Song.toMediaEntity(progressive: Int, mediaId: MediaId) : MediaEntity {
    return MediaEntity(
            this.id,
            progressive,
            MediaId.playableItem(mediaId, this.id),
            this.artistId,
            this.albumId,
            this.title,
            this.artist,
            this.albumArtist,
            this.album,
            this.image,
            this.duration,
            this.dateAdded,
            this.path,
            this.discNumber,
            this.trackNumber,
            mediaId.isAnyPodcast
    )
}

internal fun Podcast.toMediaEntity(progressive: Int, mediaId: MediaId) : MediaEntity {
    return MediaEntity(
            this.id,
            progressive,
            MediaId.playableItem(mediaId, this.id),
            this.artistId,
            this.albumId,
            this.title,
            this.artist,
            this.albumArtist,
            this.album,
            this.image,
            this.duration,
            this.dateAdded,
            this.path,
            this.discNumber,
            this.trackNumber,
            mediaId.isAnyPodcast
    )
}

internal fun PlayingQueueSong.toMediaEntity() : MediaEntity {
    return MediaEntity(
            this.id,
            this.idInPlaylist,
            MediaId.playableItem(parentMediaId, this.id),
            this.artistId,
            this.albumId,
            this.title,
            this.artist,
            this.albumArtist,
            this.album,
            this.image,
            this.duration,
            this.dateAdded,
            this.path,
            this.discNumber,
            this.trackNumber,
            this.isPodcast
    )
}

internal fun MediaEntity.toPlayerMediaEntity(positionInQueue: PositionInQueue, bookmark: Long) : PlayerMediaEntity {
    return PlayerMediaEntity(this, positionInQueue, bookmark)
}