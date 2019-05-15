package dev.olog.msc.core.interactor.played

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.ChunkedData
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.track.FolderGateway
import dev.olog.msc.core.gateway.track.GenreGateway
import javax.inject.Inject

class GetRecentlyAddedSongsUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val genreGateway: GenreGateway

)  {

    fun canShow(mediaId: MediaId): Boolean {
        return when (mediaId.category) {
            MediaIdCategory.GENRES -> genreGateway.canShowRecentlyAddedSongs(mediaId)
            MediaIdCategory.FOLDERS -> folderGateway.canShowRecentlyAddedSongs(mediaId)
            else -> false
        }
    }

    fun getChunk(mediaId: MediaId): ChunkedData<Song> {
        return when (mediaId.category) {
            MediaIdCategory.GENRES -> genreGateway.getRecentlyAddedSongsChunk(mediaId)
            MediaIdCategory.FOLDERS -> folderGateway.getRecentlyAddedSongsChunk(mediaId)
            else -> throw IllegalArgumentException("invalid category ${mediaId.category}")
        }
    }

    fun getSize(mediaId: MediaId): Int {
        return when (mediaId.category) {
            MediaIdCategory.GENRES -> genreGateway.getRecentlyAddedSongsSize(mediaId)
            MediaIdCategory.FOLDERS -> folderGateway.getRecentlyAddedSongsSize(mediaId)
            else -> throw IllegalArgumentException("invalid category ${mediaId.category}")
        }
    }

}