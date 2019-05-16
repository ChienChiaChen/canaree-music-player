package dev.olog.msc.data.mapper

import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import dev.olog.msc.core.entity.track.*
import dev.olog.msc.data.repository.queries.Columns
import dev.olog.msc.data.utils.getInt
import dev.olog.msc.data.utils.getLong
import dev.olog.msc.data.utils.getString
import dev.olog.msc.imageprovider.ImagesFolderUtils
import java.io.File

internal fun Cursor.toGenre(context: Context): Genre {
    val id = this.getLong(BaseColumns._ID)
    val name = this.getString(MediaStore.Audio.GenresColumns.NAME).capitalize()
    return Genre(
        id,
        name,
        0, // wil be updated layer
        ImagesFolderUtils.forGenre(context, id)
    )
}

internal fun Cursor.toPlaylist(context: Context): Playlist {
    val id = getLong(BaseColumns._ID)
    val name = getString(MediaStore.Audio.PlaylistsColumns.NAME).capitalize()

    return Playlist(
        id,
        name,
        0, // wil be updated layer
        ImagesFolderUtils.forPlaylist(context, id)
    )
}

internal fun Cursor.toFolder(context: Context): Folder {
    val dirPath = getString(Columns.FOLDER)
    val folderImage = ImagesFolderUtils.forFolder(context, dirPath)
    val dirName = dirPath.substring(dirPath.lastIndexOf(File.separator) + 1)

    return Folder(
        dirName.capitalize(),
        dirPath,
        getInt(Columns.N_SONGS),
        folderImage
    )
}

internal fun Cursor.toAlbum(): Album {
    return Album(
        getLong(MediaStore.Audio.Media.ALBUM_ID),
        getLong(MediaStore.Audio.Media.ARTIST_ID),
        getString(Columns.ALBUM),
        getString(Columns.ARTIST),
        getString(Columns.ALBUM_ARTIST),
        "",
        getInt(Columns.N_SONGS),
        false // TODo
    )
}

internal fun Cursor.toArtist(): Artist {
    return Artist(
        getLong(MediaStore.Audio.Media.ARTIST_ID),
        getString(Columns.ARTIST),
        getString(Columns.ALBUM_ARTIST),
        getInt(Columns.N_SONGS),
        getInt(Columns.N_ALBUMS),
        ""
    )
}