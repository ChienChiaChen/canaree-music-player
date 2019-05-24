package dev.olog.msc.presentation.dialogs.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.PlaylistType
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Page
import dev.olog.msc.core.entity.data.request.getAll
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.PlayingQueueGateway
import dev.olog.msc.core.interactor.GetSongListChunkByParamUseCase
import dev.olog.msc.core.interactor.InsertCustomTrackListRequest
import dev.olog.msc.core.interactor.InsertCustomTrackListToPlaylist
import dev.olog.msc.core.interactor.item.GetPodcastUseCase
import dev.olog.msc.core.interactor.item.GetSongUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import javax.inject.Inject

class NewPlaylistDialogViewModel @Inject constructor(
        private val insertCustomTrackListToPlaylist: InsertCustomTrackListToPlaylist,
        private val getSongListByParamUseCase: GetSongListChunkByParamUseCase,
        private val getSongUseCase: GetSongUseCase,
        private val getPodcastUseCase: GetPodcastUseCase,
        private val playingQueueGateway: PlayingQueueGateway

) : ViewModel() {

    fun executeAsync(mediaId: MediaId, playlistTitle: String) = viewModelScope.async(Dispatchers.Default) {
        val playlistType = if (mediaId.isPodcast) PlaylistType.PODCAST else PlaylistType.TRACK
        val trackToInsert = when {
            mediaId.isPlayingQueue -> playingQueueGateway.getAll(Page.NO_PAGING).map { it.id }
            mediaId.isLeaf && mediaId.isPodcast -> listOf(getPodcastUseCase.execute(mediaId).getItem()!!.id)
            mediaId.isLeaf -> listOf(getSongUseCase.execute(mediaId).getItem()!!.id)
            else -> getSongListByParamUseCase.execute(mediaId).getAll(Filter.NO_FILTER).map {
                when (it) {
                    is Song -> it.id
                    is Podcast -> it.id
                    else -> throw Exception("not supposed to happpen")
                }
            }
        }
        insertCustomTrackListToPlaylist.execute(InsertCustomTrackListRequest(playlistTitle, trackToInsert, playlistType))
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

}