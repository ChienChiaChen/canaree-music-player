package dev.olog.msc.data.mapper

import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.entity.podcast.PodcastArtist
import dev.olog.msc.data.repository.queries.Columns
import dev.olog.msc.data.utils.getInt
import dev.olog.msc.data.utils.getLong
import dev.olog.msc.data.utils.getString

internal fun Cursor.toPodcast(): Podcast {
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

    val track = getInt(Columns.N_TRACK)
    val disc = getInt(Columns.N_DISC)

    return Podcast(
        id, artistId, albumId, title, artist, albumArtist, album,
        duration, dateAdded, path, disc, track
    )
}

internal fun Cursor.toUneditedPodcast(): Podcast {
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

    val track = getInt(Columns.N_TRACK)
    val disc = getInt(Columns.N_DISC)

    return Podcast(
        id, artistId, albumId, title, artist, albumArtist, album,
        duration, dateAdded, path, disc, track
    )
}

internal fun Cursor.toPodcastAlbum(): PodcastAlbum {
    return PodcastAlbum(
        getLong(MediaStore.Audio.Media.ALBUM_ID),
        getLong(MediaStore.Audio.Media.ARTIST_ID),
        getString(Columns.ALBUM),
        getString(Columns.ARTIST),
        getString(Columns.ALBUM_ARTIST),
        getInt(Columns.N_SONGS)
    )
}

internal fun Cursor.toPodcastArtist(): PodcastArtist {
    return PodcastArtist(
        getLong(MediaStore.Audio.Media.ARTIST_ID),
        getString(Columns.ARTIST),
        getString(Columns.ALBUM_ARTIST),
        getInt(Columns.N_SONGS),
        getInt(Columns.N_ALBUMS)
    )
}