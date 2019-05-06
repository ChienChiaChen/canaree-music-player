package dev.olog.msc.presentation.playing.queue

import androidx.lifecycle.ViewModel
import dev.olog.msc.R
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.PlayingQueueSong
import dev.olog.msc.core.gateway.prefs.MusicPreferencesGateway
import dev.olog.msc.core.interactor.queue.ObservePlayingQueueUseCase
import dev.olog.msc.presentation.playing.queue.model.DisplayableQueueSong
import dev.olog.msc.shared.TrackUtils
import dev.olog.msc.shared.extensions.debounceFirst
import dev.olog.presentation.base.extensions.asLiveData
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject


class PlayingQueueFragmentViewModel @Inject constructor(
        private val musicPreferencesUseCase: MusicPreferencesGateway,
        observePlayingQueueUseCase: ObservePlayingQueueUseCase

) : ViewModel() {

    fun getCurrentPosition() = musicPreferencesUseCase.getLastPositionInQueue()

    val data = Observables.combineLatest(
            observePlayingQueueUseCase.execute().debounceFirst().distinctUntilChanged(),
            musicPreferencesUseCase.observeLastPositionInQueue().distinctUntilChanged()
    ) { queue, positionInQueue ->
        queue.mapIndexed { index, item -> item.toDisplayableItem(index, positionInQueue) }
    }
            .asLiveData()

    private fun PlayingQueueSong.toDisplayableItem(position: Int, currentItemIndex: Int): DisplayableQueueSong {
        val positionInList = when {
            currentItemIndex == -1 -> "-"
            position > currentItemIndex -> "+${position - currentItemIndex}"
            position < currentItemIndex -> "${position - currentItemIndex}"
            else -> "-"
        }

        return DisplayableQueueSong(
                R.layout.item_playing_queue,
                MediaId.songId(this.idInPlaylist.toLong()),
                title,
                TrackUtils.adjustArtist(artist),
                image,
                positionInList,
                position == currentItemIndex
        )
    }
}