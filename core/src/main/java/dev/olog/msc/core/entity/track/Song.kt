package dev.olog.msc.core.entity.track

import java.io.File

data class Song(
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
    val discNumber: Int,
    val trackNumber: Int
) {

    val folderPath: String
        get() = path.substring(0, path.lastIndexOf(File.separator))

}