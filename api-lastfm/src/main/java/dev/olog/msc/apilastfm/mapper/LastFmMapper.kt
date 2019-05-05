package dev.olog.msc.apilastfm.mapper

import dev.olog.msc.apilastfm.album.info.AlbumInfo
import dev.olog.msc.apilastfm.album.search.AlbumSearch
import dev.olog.msc.apilastfm.artist.info.ArtistInfo
import dev.olog.msc.apilastfm.track.info.TrackInfo
import dev.olog.msc.apilastfm.track.search.TrackSearch
import dev.olog.msc.core.entity.*
import dev.olog.msc.data.entity.*
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

internal fun LastFmPodcastEntity.toDomain(): LastFmPodcast {
    return LastFmPodcast(
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

internal fun LastFmPodcastAlbumEntity.toDomain(): LastFmPodcastAlbum {
    return LastFmPodcastAlbum(
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

internal fun LastFmPodcast.toModel(): LastFmPodcastEntity {
    return LastFmPodcastEntity(
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

internal fun LastFmPodcastAlbum.toModel(): LastFmPodcastAlbumEntity {
    return LastFmPodcastAlbumEntity(
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

internal fun LastFmPodcastArtistEntity.toDomain(): LastFmPodcastArtist {
    return LastFmPodcastArtist(
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

    internal fun createNullPodcast(trackId: Long): LastFmPodcastEntity {
        return LastFmPodcastEntity(
                trackId,
                "",
                "",
                "",
                "",
                millisToFormattedDate(System.currentTimeMillis())
        )
    }

    internal fun createNullPodcastArtist(artistId: Long): LastFmPodcastArtistEntity {
        return LastFmPodcastArtistEntity(
                artistId,
                "",
                millisToFormattedDate(System.currentTimeMillis())
        )
    }

    internal fun createNullPodcastAlbum(albumId: Long): LastFmPodcastAlbumEntity {
        return LastFmPodcastAlbumEntity(
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


internal fun ArtistInfo.toPodcastDomain(id: Long): LastFmPodcastArtist {
    val artist = this.artist
    return LastFmPodcastArtist(
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

internal fun AlbumSearch.toPodcastDomain(id: Long, originalArtist: String): LastFmPodcastAlbum {
    val results = this.results.albummatches.album
    val bestArtist = FuzzySearch.extractOne(originalArtist, results.map { it.artist }).string
    val best = results.first { it.artist == bestArtist }

    return LastFmPodcastAlbum(
            id,
            best.name,
            best.artist,
            best.image.reversed().first { it.text.isNotBlank() }.text
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

internal fun TrackSearch.toDomainPodcast(id: Long): LastFmPodcast {
    val track = this.results.trackmatches.track[0]

    return LastFmPodcast(
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

internal fun AlbumInfo.toPodcastDomain(id: Long): LastFmPodcastAlbum {
    val album = this.album
    return LastFmPodcastAlbum(
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

internal fun TrackInfo.toDomainPodcast(id: Long): LastFmPodcast {
    val track = this.track
    val title = track.name
    val artist = track.artist.name
    val album = track.album.title
    val image = track.album.image.reversed().first { it.text.isNotBlank() }.text

    return LastFmPodcast(
            id,
            title ?: "",
            artist ?: "",
            album ?: "",
            image
    )
}
