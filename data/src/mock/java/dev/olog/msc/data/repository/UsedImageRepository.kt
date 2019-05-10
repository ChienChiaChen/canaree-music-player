package dev.olog.msc.data.repository

import dev.olog.msc.core.entity.UsedAlbumImage
import dev.olog.msc.core.entity.UsedArtistImage
import dev.olog.msc.core.entity.UsedTrackImage
import dev.olog.msc.core.gateway.UsedImageGateway
import javax.inject.Inject

internal class UsedImageRepository @Inject constructor() : UsedImageGateway {

    override fun getAllForTracks(): List<UsedTrackImage> {
        return listOf()
    }

    override fun getAllForAlbums(): List<UsedAlbumImage> {
        return listOf()
    }

    override fun getAllForArtists(): List<UsedArtistImage> {
        return listOf()
    }

    override fun getForTrack(id: Long): String? {
        return null
    }

    override fun getForAlbum(id: Long): String? {
        return null
    }

    override fun getForArtist(id: Long): String? {
        return null
    }

    override fun setForTrack(id: Long, image: String?) {

    }

    override fun setForAlbum(id: Long, image: String?) {

    }

    override fun setForArtist(id: Long, image: String?) {
    }
}