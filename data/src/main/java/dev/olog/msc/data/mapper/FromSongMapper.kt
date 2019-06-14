package dev.olog.msc.data.mapper

import dev.olog.msc.core.entity.Album
import dev.olog.msc.core.entity.Artist
import dev.olog.msc.core.entity.Folder
import dev.olog.msc.core.entity.Song

fun Song.toFolder(songCount: Int) : Folder {
    return Folder(
            this.folder.capitalize(),
            this.folderPath,
            songCount
    )
}

fun Song.toAlbum(songCount: Int) : Album {
    return Album(
            this.albumId,
            this.artistId,
            this.album,
            this.artist,
            this.albumArtist,
            songCount,
            this.hasAlbumNameAsFolder
    )
}

fun Song.toArtist(songCount: Int, albumsCount: Int) : Artist {
    return Artist(
            this.artistId,
            this.artist,
            this.albumArtist,
            songCount,
            albumsCount
    )
}