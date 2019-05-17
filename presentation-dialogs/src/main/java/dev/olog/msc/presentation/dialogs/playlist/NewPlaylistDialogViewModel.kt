package dev.olog.msc.presentation.dialogs.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.gateway.PlayingQueueGateway
import dev.olog.msc.core.interactor.GetPlaylistsBlockingUseCase
import dev.olog.msc.core.interactor.GetSongListByParamUseCase
import dev.olog.msc.core.interactor.InsertCustomTrackListToPlaylist
import dev.olog.msc.core.interactor.item.GetPodcastUseCase
import dev.olog.msc.core.interactor.item.GetSongUseCase
import kotlinx.coroutines.cancel
import javax.inject.Inject

class NewPlaylistDialogViewModel @Inject constructor(
        playlists: GetPlaylistsBlockingUseCase,
        private val insertCustomTrackListToPlaylist: InsertCustomTrackListToPlaylist,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val getSongUseCase: GetSongUseCase,
        private val getPodcastUseCase: GetPodcastUseCase,
        private val playingQueueGateway: PlayingQueueGateway

) : ViewModel() {

//    private val existingPlaylists = playlists.execute(playlistType)
//            .map { it.title.toLowerCase() }

    fun execute(mediaId: MediaId, playlistTitle: String) = = viewModelScope.launch(Dispatchers.Default) {
//        val playlistType = if (mediaId.isPodcast) PlaylistType.PODCAST else PlaylistType.TRACK TODO
//        val trackToInsert = when {
//            mediaId.isPlayingQueue -> playingQueueGateway.getAll().map { it.id }
//            mediaId.isLeaf && mediaId.isPodcast -> getPodcastUseCase.execute(mediaId).map { listOf(it.id) }
//            mediaId.isLeaf -> getSongUseCase.execute(mediaId).map { listOf(it.id) }
//            else -> getSongListByParamUseCase.execute(mediaId).mapToList { it.id }
//        }
//        insertCustomTrackListToPlaylist.execute(InsertCustomTrackListRequest(playlistTitle, trackToInsert, playlistType))
    }

    fun isStringValid(string: String): Boolean {
//        return !existingPlaylists.contains(string.toLowerCase())
        return false
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

}