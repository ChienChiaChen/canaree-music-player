package dev.olog.msc.presentation.dialogs.play.later

import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaControllerCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.getAll
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.interactor.GetSongListChunkByParamUseCase
import dev.olog.msc.shared.MusicConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import javax.inject.Inject

class PlayLaterDialogViewModel @Inject constructor(
        private val getSongListByParamUseCase: GetSongListChunkByParamUseCase
) : ViewModel() {

    fun executeAsync(mediaId: MediaId, mediaController: MediaControllerCompat) = viewModelScope.async(Dispatchers.Default) {
        val itemsId = if (mediaId.isLeaf) {
            mediaId.leaf!!.toString()
        } else {
            val songsIds = getSongListByParamUseCase.execute(mediaId).getAll(Filter.NO_FILTER)
                    .map {
                        when (it) {
                            is Song -> it.id
                            is Podcast -> it.id
                            else -> Exception("not supposed not happen")
                        }
                    }
            songsIds.joinToString()
        }
        mediaController.addQueueItem(newMediaDescriptionItem(mediaId, itemsId))

    }

    private fun newMediaDescriptionItem(mediaId: MediaId, songId: String): MediaDescriptionCompat {
        return MediaDescriptionCompat.Builder()
                .setMediaId(songId)
                .setExtras(bundleOf(MusicConstants.IS_PODCAST to mediaId.isAnyPodcast))
                .build()
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

}