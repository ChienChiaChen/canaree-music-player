package dev.olog.msc.presentation.dialogs.rename

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.interactor.GetPlaylistsBlockingUseCase
import kotlinx.coroutines.cancel
import javax.inject.Inject

class RenameDialogViewModel @Inject constructor(
    getPlaylistSiblingsUseCase: GetPlaylistsBlockingUseCase,
    private val renameUseCase: RenameUseCase

) : ViewModel() {

//    private val existingPlaylists = getPlaylistSiblingsUseCase
//        .execute(if (mediaId.isPodcast) PlaylistType.PODCAST else PlaylistType.TRACK)
//        .map { it.title }
//        .map { it.toLowerCase() } TODO

    fun execute(mediaId: MediaId, newTitle: String) = = viewModelScope.launch(Dispatchers.Default) {
        renameUseCase.execute(Pair(mediaId, newTitle))
    }

    /**
     * returns false if is invalid
     */
    fun checkData(playlistTitle: String): Boolean {
//        return when {
//            mediaId.isPlaylist || mediaId.isPodcastPlaylist -> !existingPlaylists.contains(playlistTitle.toLowerCase())
//            else -> throw IllegalArgumentException("invalid media id category $mediaId")
//        }
        return false
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

}