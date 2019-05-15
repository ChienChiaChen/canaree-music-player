package dev.olog.msc.core.interactor

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.ChunkedData
import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.gateway.track.FolderGateway
import dev.olog.msc.core.gateway.track.GenreGateway
import dev.olog.msc.core.gateway.track.PlaylistGateway
import javax.inject.Inject

class GetRelatedArtistsUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val genreGateway: GenreGateway
) {

    fun canShow(mediaId: MediaId): Boolean {
        // TODO do podcast?
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.canShowRelatedArtists(mediaId)
            MediaIdCategory.PLAYLISTS -> playlistGateway.canShowRelatedArtists(mediaId)
            MediaIdCategory.GENRES -> genreGateway.canShowRelatedArtists(mediaId)
            else -> false
        }
    }

    fun getChunk(mediaId: MediaId): ChunkedData<Artist> {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.getRelatedArtistsChunk(mediaId)
            MediaIdCategory.PLAYLISTS -> playlistGateway.getRelatedArtistsChunk(mediaId)
            MediaIdCategory.GENRES -> genreGateway.getRelatedArtistsChunk(mediaId)
            else -> throw IllegalArgumentException("invalid category ${mediaId.category}")
        }
    }

    fun getSize(mediaId: MediaId): Int {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.getRelatedArtistsSize(mediaId)
            MediaIdCategory.PLAYLISTS -> playlistGateway.getRelatedArtistsSize(mediaId)
            MediaIdCategory.GENRES -> genreGateway.getRelatedArtistsSize(mediaId)
            else -> throw IllegalArgumentException("invalid category ${mediaId.category}")
        }
    }

}