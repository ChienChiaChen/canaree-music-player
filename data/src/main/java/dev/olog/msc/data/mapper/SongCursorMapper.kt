package dev.olog.msc.data.mapper

import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.data.repository.queries.Columns
import dev.olog.msc.data.utils.getInt
import dev.olog.msc.data.utils.getLong
import dev.olog.msc.data.utils.getString


internal fun Cursor.toSong(): Song {
    val id = getLong(BaseColumns._ID)
    val artistId = getLong(MediaStore.Audio.AudioColumns.ARTIST_ID)
    val albumId = getLong(MediaStore.Audio.AudioColumns.ALBUM_ID)

    val path = getString(MediaStore.MediaColumns.DATA)

    val title = getString(MediaStore.MediaColumns.TITLE)

    val artist = getString(Columns.ARTIST)
    val album = getString(Columns.ALBUM)
    val albumArtist = getString(Columns.ALBUM_ARTIST)

    val duration = getLong(MediaStore.Audio.AudioColumns.DURATION)
    val dateAdded = getLong(MediaStore.MediaColumns.DATE_ADDED)

    val trackNumber = getInt(MediaStore.Audio.AudioColumns.TRACK)
    val track = extractTrackNumber(trackNumber)
    val disc = extractDiscNumber(trackNumber)

    return Song(
        id, artistId, albumId, title, artist, albumArtist, album,
        "", duration, dateAdded, path, disc, track
    )
}

internal fun Cursor.toUneditedSong(image: String): Song {
    val id = getLong(BaseColumns._ID)
    val artistId = getLong(MediaStore.Audio.AudioColumns.ARTIST_ID)
    val albumId = getLong(MediaStore.Audio.AudioColumns.ALBUM_ID)

    val path = getString(MediaStore.MediaColumns.DATA)

    val title = getString(MediaStore.MediaColumns.TITLE)

    val artist = getString(Columns.ARTIST)
    val album = getString(Columns.ALBUM)
    val albumArtist = getString(Columns.ALBUM_ARTIST)

    val duration = getLong(MediaStore.Audio.AudioColumns.DURATION)
    val dateAdded = getLong(MediaStore.MediaColumns.DATE_ADDED)

    val trackNumber = getInt(MediaStore.Audio.AudioColumns.TRACK)
    val track = extractTrackNumber(trackNumber)
    val disc = extractDiscNumber(trackNumber)

    return Song(
        id, artistId, albumId, title, artist, albumArtist, album,
        image, duration, dateAdded, path, disc, track
    )
}

internal fun extractTrackNumber(originalTrackNumber: Int): Int {
    if (originalTrackNumber >= 1000) {
        return originalTrackNumber % 1000
    }
    return originalTrackNumber
}

internal fun extractDiscNumber(originalTrackNumber: Int): Int {
    if (originalTrackNumber >= 1000) {
        return originalTrackNumber / 1000
    }
    return 0
}

//internal fun adjustAlbum(album: String, folder: String): String {
//    val hasUnknownAlbum = album == folder
//    return if (hasUnknownAlbum) {
//        AppConstants.UNKNOWN
//    } else {
//        album
//    }
//}
