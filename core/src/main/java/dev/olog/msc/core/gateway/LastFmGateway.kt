package dev.olog.msc.core.gateway

import com.github.dmstocking.optional.java.util.Optional
import dev.olog.msc.core.entity.*
import io.reactivex.Single

interface LastFmGateway {

    companion object {
        const val LAST_FM_API_KEY = "56553f687cba2aa671c99caff536def1"
        const val LAST_FM_API_SECRET = "b81ab835211b283398a16343ab0e6b66"
    }

    fun shouldFetchTrack(trackId: Long): Single<Boolean>
    fun getTrack(trackId: Long): Single<Optional<LastFmTrack?>>
    fun deleteTrack(trackId: Long)

    fun shouldFetchTrackImage(trackId: Long): Single<Boolean>
    fun getTrackImage(trackId: Long): Single<Optional<String?>>

    fun shouldFetchAlbum(albumId: Long): Single<Boolean>
    fun getAlbum(albumId: Long): Single<Optional<LastFmAlbum?>>
    fun deleteAlbum(albumId: Long)

    fun shouldFetchArtist(artistId: Long): Single<Boolean>
    fun getArtist(artistId: Long): Single<Optional<LastFmArtist?>>
    fun deleteArtist(artistId: Long)

    // podcast

    fun shouldFetchPodcast(podcastId: Long): Single<Boolean>
    fun getPodcast(podcastId: Long): Single<Optional<LastFmPodcast?>>
    fun deletePodcast(podcastId: Long)

    fun shouldFetchPodcastImage(podcastId: Long): Single<Boolean>
    fun getPodcastImage(podcastId: Long): Single<Optional<String?>>

    fun shouldFetchPodcastAlbum(podcastId: Long): Single<Boolean>
    fun getPodcastAlbum(podcastId: Long): Single<Optional<LastFmPodcastAlbum?>>
    fun deletePodcastAlbum(podcastId: Long)

    fun shouldFetchPodcastArtist(podcastId: Long): Single<Boolean>
    fun getPodcastArtist(podcastId: Long): Single<Optional<LastFmPodcastArtist?>>
    fun deletePodcastArtist(podcastId: Long)

}