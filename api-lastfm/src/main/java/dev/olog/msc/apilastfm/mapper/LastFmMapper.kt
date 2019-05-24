package dev.olog.msc.apilastfm.mapper

import dev.olog.msc.apilastfm.album.info.AlbumInfo
import dev.olog.msc.apilastfm.album.search.AlbumSearch
import dev.olog.msc.apilastfm.artist.info.ArtistInfo
import dev.olog.msc.apilastfm.track.info.TrackInfo
import dev.olog.msc.apilastfm.track.search.TrackSearch
import dev.olog.msc.core.entity.LastFmAlbum
import dev.olog.msc.core.entity.LastFmArtist
import dev.olog.msc.core.entity.LastFmTrack
import dev.olog.msc.data.entity.LastFmAlbumEntity
import dev.olog.msc.data.entity.LastFmArtistEntity
import dev.olog.msc.data.entity.LastFmPodcastArtistEntity
import dev.olog.msc.data.entity.LastFmTrackEntity
import me.xdrop.fuzzywuzzy.FuzzySearch
import java.text.SimpleDateFormat
import java.util.*

private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

private fun millisToFormattedDate(value: Long): String {
    return formatter.format(Date(value))

}

internal fun LastFmTrackEntity.toDomain(): LastFmTrack {
    return LastFmTrack(
        this.id,
        this.title,
        this.artist,
        this.album,
        this.image
    )
}

internal fun LastFmAlbumEntity.toDomain(): LastFmAlbum {
    return LastFmAlbum(
        this.id,
        this.title,
        this.artist,
        this.image
    )
}

internal fun LastFmTrack.toModel(): LastFmTrackEntity {
    return LastFmTrackEntity(
        this.id,
        this.title,
        this.artist,
        this.album,
        this.image,
        millisToFormattedDate(System.currentTimeMillis())
    )
}


internal fun LastFmAlbum.toModel(): LastFmAlbumEntity {
    return LastFmAlbumEntity(
        this.id,
        this.title,
        this.artist,
        this.image,
        millisToFormattedDate(System.currentTimeMillis())
    )
}

internal fun LastFmArtistEntity.toDomain(): LastFmArtist {
    return LastFmArtist(
        this.id,
        this.image
    )
}

internal object LastFmNulls {

    internal fun createNullTrack(trackId: Long): LastFmTrackEntity {
        return LastFmTrackEntity(
            trackId,
            "",
            "",
            "",
            "",
            millisToFormattedDate(System.currentTimeMillis())
        )
    }

    internal fun createNullArtist(artistId: Long): LastFmArtistEntity {
        return LastFmArtistEntity(
            artistId,
            "",
            millisToFormattedDate(System.currentTimeMillis())
        )
    }

    internal fun createNullAlbum(albumId: Long): LastFmAlbumEntity {
        return LastFmAlbumEntity(
            albumId,
            "",
            "",
            "",
            millisToFormattedDate(System.currentTimeMillis())
        )
    }

}


internal fun ArtistInfo.toDomain(id: Long): LastFmArtist {
    val artist = this.artist
    return LastFmArtist(
        id,
        artist.image.reversed().first { it.text.isNotBlank() }.text
    )
}


internal fun ArtistInfo.toModel(id: Long): LastFmArtistEntity {
    val artist = this.artist
    return LastFmArtistEntity(
        id,
        artist.image.reversed().first { it.text.isNotBlank() }.text,
        millisToFormattedDate(System.currentTimeMillis())
    )
}

internal fun ArtistInfo.toPodcastModel(id: Long): LastFmPodcastArtistEntity {
    val artist = this.artist
    return LastFmPodcastArtistEntity(
        id,
        artist.image.reversed().first { it.text.isNotBlank() }.text,
        millisToFormattedDate(System.currentTimeMillis())
    )
}

internal fun AlbumSearch.toDomain(id: Long, originalArtist: String): LastFmAlbum {
    val results = this.results.albummatches.album
    val bestArtist = FuzzySearch.extractOne(originalArtist, results.map { it.artist }).string
    val best = results.first { it.artist == bestArtist }

    return LastFmAlbum(
        id,
        best.name,
        best.artist,
        best.image.reversed().first { it.text.isNotBlank() }.text
    )
}

internal fun TrackSearch.toDomain(id: Long): LastFmTrack {
    val track = this.results.trackmatches.track[0]

    return LastFmTrack(
        id,
        track.name ?: "",
        track.artist ?: "",
        "",
        ""
    )
}


internal fun AlbumInfo.toDomain(id: Long): LastFmAlbum {
    val album = this.album
    return LastFmAlbum(
        id,
        album.name,
        album.artist,
        album.image.reversed().first { it.text.isNotBlank() }.text
    )
}

internal fun TrackInfo.toDomain(id: Long): LastFmTrack {
    val track = this.track
    val title = track.name
    val artist = track.artist.name
    val album = track.album.title
    val image = track.album.image.reversed().first { it.text.isNotBlank() }.text

    return LastFmTrack(
        id,
        title ?: "",
        artist ?: "",
        album ?: "",
        image
    )
}
