package dev.olog.msc.data.mapper

import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import dev.olog.msc.core.entity.Genre
import dev.olog.msc.core.entity.Playlist
import dev.olog.msc.data.entity.PlaylistSongEntity
import dev.olog.msc.data.utils.getLong
import dev.olog.msc.data.utils.getLongOrNull
import dev.olog.msc.data.utils.getStringOrNull

fun Cursor.toGenre(genreSize: Int) : Genre {
    val id = this.getLongOrNull(BaseColumns._ID) ?: -1
    val name = this.getStringOrNull(MediaStore.Audio.GenresColumns.NAME)?.capitalize() ?: ""
    return Genre(
            id,
            name,
            genreSize
    )
}

fun Cursor.toPlaylist(playlistSize: Int) : Playlist {
    val id = this.getLongOrNull(BaseColumns._ID) ?: -1
    val name = this.getStringOrNull(MediaStore.Audio.PlaylistsColumns.NAME)?.capitalize() ?: ""

    return Playlist(
            id,
            name,
            playlistSize
    )
}

fun Cursor.extractId() : Long {
    return this.getLong(BaseColumns._ID)
}

fun Cursor.toPlaylistSong() : PlaylistSongEntity {
    return PlaylistSongEntity(
        this.getLong(MediaStore.Audio.Playlists.Members._ID),
        this.getLong(MediaStore.Audio.Playlists.Members.AUDIO_ID)
    )
}