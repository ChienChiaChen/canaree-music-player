package dev.olog.msc.core.interactor

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.data.request.DataRequest
import dev.olog.msc.core.entity.data.request.Filter
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

    fun canShow(mediaId: MediaId, filter: Filter): Boolean {
        // TODO do podcast?
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.canShowRelatedArtists(mediaId, filter)
            MediaIdCategory.PLAYLISTS -> playlistGateway.canShowRelatedArtists(mediaId, filter)
            MediaIdCategory.GENRES -> genreGateway.canShowRelatedArtists(mediaId, filter)
            else -> false
        }
    }

    fun get(mediaId: MediaId): DataRequest<Artist> {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.getRelatedArtists(mediaId)
            MediaIdCategory.PLAYLISTS -> playlistGateway.getRelatedArtists(mediaId)
            MediaIdCategory.GENRES -> genreGateway.getRelatedArtists(mediaId)
            else -> throw IllegalArgumentException("invalid category ${mediaId.category}")
        }
    }

}