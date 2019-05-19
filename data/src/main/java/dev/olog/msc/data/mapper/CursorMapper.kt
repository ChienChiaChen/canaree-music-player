package dev.olog.msc.data.mapper

import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import dev.olog.msc.core.entity.track.*
import dev.olog.msc.data.repository.queries.Columns
import dev.olog.msc.data.utils.getInt
import dev.olog.msc.data.utils.getLong
import dev.olog.msc.data.utils.getString
import java.io.File

internal fun Cursor.toGenre(): Genre {
    val id = this.getLong(BaseColumns._ID)
    val name = this.getString(MediaStore.Audio.GenresColumns.NAME).capitalize()
    return Genre(
        id,
        name,
        0 // wil be updated later
    )
}

internal fun Cursor.toPlaylist(): Playlist {
    val id = getLong(BaseColumns._ID)
    val name = getString(MediaStore.Audio.PlaylistsColumns.NAME).capitalize()

    return Playlist(
        id,
        name,
        0 // wil be updated later
    )
}

internal fun Cursor.toFolder(): Folder {
    val dirPath = getString(Columns.FOLDER)
    val dirName = dirPath.substring(dirPath.lastIndexOf(File.separator) + 1)

    return Folder(
        dirName.capitalize(),
        dirPath,
        getInt(Columns.N_SONGS)
    )
}

internal fun Cursor.toAlbum(): Album {
    val title = getString(Columns.ALBUM)
    val folder = getString(Columns.FOLDER)
    val hasSameNameAsFolder = folder.endsWith(title)

    return Album(
        getLong(MediaStore.Audio.Media.ALBUM_ID),
        getLong(MediaStore.Audio.Media.ARTIST_ID),
        getString(Columns.ALBUM),
        getString(Columns.ARTIST),
        getString(Columns.ALBUM_ARTIST),
        getInt(Columns.N_SONGS),
        hasSameNameAsFolder
    )
}

internal fun Cursor.toArtist(): Artist {
    return Artist(
        getLong(MediaStore.Audio.Media.ARTIST_ID),
        getString(Columns.ARTIST),
        getString(Columns.ALBUM_ARTIST),
        getInt(Columns.N_SONGS),
        getInt(Columns.N_ALBUMS)
    )
}