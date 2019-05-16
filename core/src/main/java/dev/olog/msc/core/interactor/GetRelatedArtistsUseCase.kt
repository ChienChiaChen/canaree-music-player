package dev.olog.msc.core.interactor

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.PageRequest
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

    fun get(mediaId: MediaId): PageRequest<Artist> {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.getRelatedArtists(mediaId)
            MediaIdCategory.PLAYLISTS -> playlistGateway.getRelatedArtists(mediaId)
            MediaIdCategory.GENRES -> genreGateway.getRelatedArtists(mediaId)
            else -> throw IllegalArgumentException("invalid category ${mediaId.category}")
        }
    }

}