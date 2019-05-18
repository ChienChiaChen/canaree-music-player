package dev.olog.msc.presentation.dialogs.play.next

import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaControllerCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.interactor.GetSongListByParamUseCase
import dev.olog.msc.shared.MusicConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlayNextDialogViewModel @Inject constructor(
    private val mediaId: MediaId,
    private val getSongListByParamUseCase: GetSongListByParamUseCase
) : ViewModel() {

    fun execute(mediaController: MediaControllerCompat) = viewModelScope.launch(Dispatchers.Default) {
        TODO()
//        return if (mediaId.isLeaf) {
//            Single.fromCallable { "${mediaId.leaf!!}" }.subscribeOn(Schedulers.io())
//        } else {
//            getSongListByParamUseCase.execute(mediaId)
//                .firstOrError()
//                .map { songList -> songList.asSequence().map { it.id }.joinToString() }
//        }.map { mediaController.addQueueItem(newMediaDescriptionItem(it), Int.MAX_VALUE) }
//            .ignoreElement()
    }

    private fun newMediaDescriptionItem(songId: String): MediaDescriptionCompat {
        return MediaDescriptionCompat.Builder()
            .setMediaId(songId)
            .setExtras(bundleOf(MusicConstants.IS_PODCAST to mediaId.isAnyPodcast))
            .build()
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

}