package dev.olog.msc.data.mapper

import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import dev.olog.msc.core.entity.track.*
import dev.olog.msc.data.entity.PlaylistSongEntity
import dev.olog.msc.data.utils.*
import dev.olog.msc.data.utils.getLong
import dev.olog.msc.data.utils.getLongOrNull
import dev.olog.msc.data.utils.getString
import dev.olog.msc.data.utils.getStringOrNull
import dev.olog.msc.imageprovider.ImagesFolderUtils
import java.io.File

internal fun Cursor.toGenre(context: Context, genreSize: Int) : Genre {
    val id = this.getLongOrNull(BaseColumns._ID) ?: -1
    val name = this.getStringOrNull(MediaStore.Audio.GenresColumns.NAME)?.capitalize() ?: ""
    return Genre(
            id,
            name,
            genreSize,
            ImagesFolderUtils.forGenre(context, id)
    )
}

internal fun Cursor.toPlaylist(context: Context, playlistSize: Int) : Playlist {
    val id = this.getLongOrNull(BaseColumns._ID) ?: -1
    val name = this.getStringOrNull(MediaStore.Audio.PlaylistsColumns.NAME)?.capitalize() ?: ""

    return Playlist(
            id,
            name,
            playlistSize,
            ImagesFolderUtils.forPlaylist(context, id)
    )
}

internal fun Cursor.extractId() : Long {
    return this.getLong(BaseColumns._ID)
}

internal fun Cursor.toPlaylistSong() : PlaylistSongEntity {
    return PlaylistSongEntity(
            this.getLong(MediaStore.Audio.Playlists.Members._ID),
            this.getLong(MediaStore.Audio.Playlists.Members.AUDIO_ID)
    )
}

internal fun Cursor.toFolder(context: Context): Folder {
    val dirPath = getString("folder")
    val folderImage = ImagesFolderUtils.forFolder(context, dirPath)
    val dirName = dirPath.substring(dirPath.lastIndexOf(File.separator) + 1)

    return Folder(
        dirName.capitalize(),
        dirPath,
        getInt("songs"),
        folderImage
    )
}

internal fun Cursor.toAlbum(): Album {
    val artist = getString(MediaStore.Audio.Media.ARTIST)
    var albumArtist = artist
    val albumArtistIndex = this.getColumnIndex("album_artist")
    if (albumArtistIndex != -1) {
        this.getStringOrNull(albumArtistIndex)?.also {
            albumArtist = it
        }
    }

    return Album(
        getLong(MediaStore.Audio.Media.ALBUM_ID),
        getLong(MediaStore.Audio.Media.ARTIST_ID),
        getString(MediaStore.Audio.Media.ALBUM),
        artist,
        albumArtist,
        "",
        getInt("songs"),
        false // TODo
    )
}

internal fun Cursor.toArtist(): Artist {
    val artist = getString(MediaStore.Audio.Media.ARTIST)
    var albumArtist = artist
    val albumArtistIndex = this.getColumnIndex("album_artist")
    if (albumArtistIndex != -1) {
        this.getStringOrNull(albumArtistIndex)?.also {
            albumArtist = it
        }
    }

    return Artist(
        getLong(MediaStore.Audio.Media.ARTIST_ID),
        artist,
        albumArtist,
        getInt("songs"),
        getInt("albums"),
        ""
    )
}