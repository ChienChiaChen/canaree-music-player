package dev.olog.msc.data.api.last.fm

import androidx.annotation.IntRange
import dev.olog.msc.data.BuildConfig
import dev.olog.msc.data.api.last.fm.album.info.AlbumInfo
import dev.olog.msc.data.api.last.fm.album.search.AlbumSearch
import dev.olog.msc.data.api.last.fm.artist.info.ArtistInfo
import dev.olog.msc.data.api.last.fm.artist.search.ArtistSearch
import dev.olog.msc.data.api.last.fm.track.info.TrackInfo
import dev.olog.msc.data.api.last.fm.track.search.TrackSearch
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query


private const val MIN_SEARCH_PAGES = 1L
private const val MAX_SEARCH_PAGES = 5L
private const val DEFAULT_SEARCH_PAGES = MAX_SEARCH_PAGES

private const val DEFAULT_AUTO_CORRECT = 1L

private const val BASE_URL = "?api_key=${BuildConfig.LAST_FM_KEY}&format=json"

interface LastFmService {

    @GET("$BASE_URL&method=track.getInfo")
    fun getTrackInfoAsync(
        @Query("track", encoded = true) track: String,
        @Query("artist", encoded = true) artist: String,
        @IntRange(from = 0, to = 1) @Query("autocorrect") autocorrect: Long = DEFAULT_AUTO_CORRECT
    ): Deferred<TrackInfo>

    @GET("$BASE_URL&method=track.search")
    fun searchTrackAsync(
        @Query("track", encoded = true) track: String,
        @Query("artist", encoded = true) artist: String = "",
        @IntRange(from = MIN_SEARCH_PAGES, to = MAX_SEARCH_PAGES)
        @Query("limit") limit: Long = DEFAULT_SEARCH_PAGES
    ): Deferred<TrackSearch>

    @GET("$BASE_URL&method=artist.getinfo")
    fun getArtistInfoAsync(
        @Query("artist", encoded = true) artist: String,
        @IntRange(from = 0, to = 1)
        @Query("autocorrect") autocorrect: Long = DEFAULT_AUTO_CORRECT,
        @Query("lang") language: String = "en"
    ): Deferred<ArtistInfo>

    @GET("$BASE_URL&method=artist.search")
    fun searchArtistAsync(
        @Query("artist", encoded = true) artist: String,
        @IntRange(from = MIN_SEARCH_PAGES, to = MAX_SEARCH_PAGES)
        @Query("limit") limit: Long = DEFAULT_SEARCH_PAGES
    ): Deferred<ArtistSearch>

    @GET("$BASE_URL&method=album.getinfo")
    fun getAlbumInfoAsync(
        @Query("album", encoded = true) album: String,
        @Query("artist", encoded = true) artist: String,
        @IntRange(from = 0, to = 1)
        @Query("autocorrect") autocorrect: Long = DEFAULT_AUTO_CORRECT,
        @Query("lang") language: String = "en"
    ): Deferred<AlbumInfo>

    @GET("$BASE_URL&method=album.search")
    fun searchAlbumAsync(
        @Query("album", encoded = true) album: String,
        @IntRange(from = MIN_SEARCH_PAGES, to = MAX_SEARCH_PAGES)
        @Query("limit") limit: Long = DEFAULT_SEARCH_PAGES
    ): Deferred<AlbumSearch>

}