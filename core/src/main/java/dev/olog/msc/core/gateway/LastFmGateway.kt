package dev.olog.msc.core.gateway

import dev.olog.msc.core.entity.LastFmAlbum
import dev.olog.msc.core.entity.LastFmArtist
import dev.olog.msc.core.entity.LastFmTrack

interface LastFmGateway {

    suspend fun shouldFetchTrack(trackId: Long): Boolean
    suspend fun getTrack(trackId: Long): LastFmTrack?
    suspend fun deleteTrack(trackId: Long)

    suspend fun shouldFetchTrackImage(trackId: Long): Boolean
    suspend fun getTrackImage(trackId: Long): String?

    suspend fun shouldFetchAlbum(albumId: Long): Boolean
    suspend fun getAlbum(albumId: Long): LastFmAlbum?
    suspend fun deleteAlbum(albumId: Long)

    suspend fun shouldFetchArtist(artistId: Long): Boolean
    suspend fun getArtist(artistId: Long): LastFmArtist?
    suspend fun deleteArtist(artistId: Long)

}