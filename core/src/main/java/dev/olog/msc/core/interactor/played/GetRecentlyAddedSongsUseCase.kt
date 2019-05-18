package dev.olog.msc.core.interactor.played

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.data.request.DataRequest
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.track.FolderGateway
import dev.olog.msc.core.gateway.track.GenreGateway
import javax.inject.Inject

class GetRecentlyAddedSongsUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val genreGateway: GenreGateway

) {

    fun canShow(mediaId: MediaId): Boolean {
        return when (mediaId.category) {
            MediaIdCategory.GENRES -> genreGateway.canShowRecentlyAddedSongs(mediaId)
            MediaIdCategory.FOLDERS -> folderGateway.canShowRecentlyAddedSongs(mediaId)
            else -> false
        }
    }

    fun get(mediaId: MediaId): DataRequest<Song> {
        return when (mediaId.category) {
            MediaIdCategory.GENRES -> genreGateway.getRecentlyAddedSongs(mediaId)
            MediaIdCategory.FOLDERS -> folderGateway.getRecentlyAddedSongs(mediaId)
            else -> throw IllegalArgumentException("invalid category ${mediaId.category}")
        }
    }
}