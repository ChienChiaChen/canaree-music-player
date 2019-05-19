package dev.olog.msc.apilastfm

import dev.olog.msc.apilastfm.album.info.AlbumInfo
import dev.olog.msc.apilastfm.album.search.AlbumSearch
import dev.olog.msc.apilastfm.annotation.Impl
import dev.olog.msc.apilastfm.artist.info.ArtistInfo
import dev.olog.msc.apilastfm.artist.search.ArtistSearch
import dev.olog.msc.apilastfm.track.info.TrackInfo
import dev.olog.msc.apilastfm.track.search.TrackSearch
import dev.olog.msc.shared.TrackUtils
import kotlinx.coroutines.Deferred
import java.net.URLEncoder
import javax.inject.Inject

internal class LastFmProxy @Inject constructor(
    @Impl private val impl: LastFmService

) : LastFmService {

    /**
     * [https://www.last.fm/api/show/track.getInfo]
     * A not unknown artist is required
     */
    override fun getTrackInfoAsync(track: String, artist: String, autocorrect: Long): Deferred<TrackInfo> {
        if (artist == TrackUtils.UNKNOWN_ARTIST) {
            throw IllegalArgumentException("artist can not be unknown")
        }

        val normalizedTrack = UTF8NormalizedEntity(track)
        val normalizedArtist = UTF8NormalizedEntity(artist)
        return impl.getTrackInfoAsync(
            normalizedTrack.value,
            normalizedArtist.value
        )
    }

    override fun searchTrackAsync(track: String, artist: String, limit: Long): Deferred<TrackSearch> {
        val normalizedTrack = UTF8NormalizedEntity(track)
        val normalizedArtist = UTF8NormalizedEntity(if (artist == TrackUtils.UNKNOWN_ARTIST) "" else artist)
        return impl.searchTrackAsync(
            normalizedTrack.value,
            normalizedArtist.value
        )
    }

    override fun getArtistInfoAsync(artist: String, autocorrect: Long, language: String): Deferred<ArtistInfo> {
        val normalizedArtist = UTF8NormalizedEntity(artist)
        return impl.getArtistInfoAsync(
            normalizedArtist.value
        )
    }

    override fun searchArtistAsync(artist: String, limit: Long): Deferred<ArtistSearch> {
        val normalizedArtist = UTF8NormalizedEntity(artist).value
        return impl.searchArtistAsync(normalizedArtist)
    }

    /**
     * [https://www.last.fm/api/show/album.getInfo]
     * A not unknown artist is required
     */
    override fun getAlbumInfoAsync(album: String, artist: String, autocorrect: Long, language: String): Deferred<AlbumInfo> {
        if (artist == TrackUtils.UNKNOWN_ARTIST) {
            throw  IllegalArgumentException("artist can not be unknown")
        }

        val normalizedAlbum = UTF8NormalizedEntity(album)
        val normalizedArtist = UTF8NormalizedEntity(artist)
        return impl.getAlbumInfoAsync(
            normalizedAlbum.value,
            normalizedArtist.value
        )
    }

    override fun searchAlbumAsync(album: String, limit: Long): Deferred<AlbumSearch> {
        val normalizedAlbum = UTF8NormalizedEntity(album).value
        return impl.searchAlbumAsync(normalizedAlbum)
    }

    private class UTF8NormalizedEntity(value: String) {
        val value: String = URLEncoder.encode(value, "UTF-8")
    }

}