package dev.olog.msc.apilastfm.data

import dev.olog.msc.core.entity.LastFmAlbum
import dev.olog.msc.core.entity.LastFmArtist
import dev.olog.msc.core.entity.LastFmTrack
import dev.olog.msc.core.gateway.LastFmGateway
import javax.inject.Inject

internal class LastFmRepository @Inject constructor(
    private val lastFmRepoTrack: LastFmRepoTrack,
    private val lastFmRepoArtist: LastFmRepoArtist,
    private val lastFmRepoAlbum: LastFmRepoAlbum

) : LastFmGateway {

    override suspend fun shouldFetchTrack(trackId: Long): Boolean {
        return lastFmRepoTrack.shouldFetch(trackId)
    }

    override suspend fun getTrack(trackId: Long): LastFmTrack? {
        return lastFmRepoTrack.get(trackId)
    }

    override suspend fun deleteTrack(trackId: Long) {
        lastFmRepoTrack.delete(trackId)
    }

    override suspend fun shouldFetchTrackImage(trackId: Long): Boolean {
        val item = lastFmRepoTrack.getOriginalItem(trackId) ?: return false

        return lastFmRepoAlbum.shouldFetch(item.albumId) || lastFmRepoTrack.shouldFetch(item.id)
    }

    override suspend fun getTrackImage(trackId: Long): String? {
        val item = lastFmRepoTrack.getOriginalItem(trackId) ?: return null

        try {
            return lastFmRepoAlbum.get(item.albumId)?.image
        } catch (ex: Exception) {
            return lastFmRepoTrack.get(trackId)?.image
        }
    }

    override suspend fun shouldFetchAlbum(albumId: Long): Boolean {
        return lastFmRepoAlbum.shouldFetch(albumId)
    }

    override suspend fun getAlbum(albumId: Long): LastFmAlbum? {
        return lastFmRepoAlbum.get(albumId)
    }

    override suspend fun deleteAlbum(albumId: Long) {
        lastFmRepoAlbum.delete(albumId)
    }

    override suspend fun shouldFetchArtist(artistId: Long): Boolean {
        return lastFmRepoArtist.shouldFetch(artistId)
    }

    override suspend fun getArtist(artistId: Long): LastFmArtist? {
        return lastFmRepoArtist.get(artistId)
    }

    override suspend fun deleteArtist(artistId: Long) {
        lastFmRepoArtist.delete(artistId)
    }
}