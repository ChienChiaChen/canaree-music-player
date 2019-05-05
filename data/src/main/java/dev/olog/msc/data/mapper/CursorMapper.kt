package dev.olog.msc.data.mapper

import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import dev.olog.msc.core.entity.track.Genre
import dev.olog.msc.core.entity.track.Playlist
import dev.olog.msc.data.entity.PlaylistSongEntity
import dev.olog.msc.data.utils.getLong
import dev.olog.msc.data.utils.getLongOrNull
import dev.olog.msc.data.utils.getStringOrNull
import dev.olog.msc.imageprovider.ImagesFolderUtils

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