package dev.olog.msc.core.gateway

import dev.olog.msc.core.entity.UsedAlbumImage
import dev.olog.msc.core.entity.UsedArtistImage
import dev.olog.msc.core.entity.UsedTrackImage

interface UsedImageGateway {

    fun getAllForTracks(): List<UsedTrackImage>
    fun getAllForAlbums(): List<UsedAlbumImage>
    fun getAllForArtists(): List<UsedArtistImage>

    fun getForTrack(id: Long): String?
    fun getForAlbum(id: Long): String?
    fun getForArtist(id: Long): String?

    fun setForTrack(id: Long, image: String?)
    fun setForAlbum(id: Long, image: String?)
    fun setForArtist(id: Long, image: String?)

}