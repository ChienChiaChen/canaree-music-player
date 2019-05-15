package dev.olog.msc.core.entity.podcast

import dev.olog.msc.core.entity.track.Song

data class Podcast (
        val id: Long,
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
        val trackNumber: Int)

fun Podcast.toSong(): Song {
    return Song(
            this.id,
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
            this.trackNumber
    )
}