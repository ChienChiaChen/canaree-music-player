package dev.olog.msc.core.interactor.played

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.ChunkedData
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.track.FolderGateway
import dev.olog.msc.core.gateway.track.GenreGateway
import dev.olog.msc.core.gateway.track.PlaylistGateway
import javax.inject.Inject

class GetMostPlayedSongsUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val genreGateway: GenreGateway

)  {

    fun canShow(mediaId: MediaId): Boolean {
        return when (mediaId.category) {
            MediaIdCategory.GENRES -> genreGateway.canShowMostPlayed(mediaId)
            MediaIdCategory.PLAYLISTS -> playlistGateway.canShowMostPlayed(mediaId)
            MediaIdCategory.FOLDERS -> folderGateway.canShowMostPlayed(mediaId)
            else -> false
        }
    }

    fun getChunk(mediaId: MediaId): ChunkedData<Song> {
        return when (mediaId.category) {
            MediaIdCategory.GENRES -> genreGateway.getMostPlayedChunk(mediaId)
            MediaIdCategory.PLAYLISTS -> playlistGateway.getMostPlayedChunk(mediaId)
            MediaIdCategory.FOLDERS -> folderGateway.getMostPlayedChunk(mediaId)
            else -> throw IllegalArgumentException("invalid category ${mediaId.category}")
        }
    }
}