package dev.olog.msc.core.gateway

interface UsedImageGateway {

    fun getForTrack(id: Long): String?
    fun getForAlbum(id: Long): String?
    fun getForArtist(id: Long): String?

    suspend fun setForTrack(id: Long, image: String?)
    suspend fun setForAlbum(id: Long, image: String?)
    suspend fun setForArtist(id: Long, image: String?)

}