package dev.olog.msc.presentation.player.mini

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.msc.core.gateway.prefs.MusicPreferencesGateway
import dev.olog.msc.shared.ui.extensions.filter
import dev.olog.msc.shared.ui.extensions.liveDataOf
import dev.olog.msc.shared.ui.extensions.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MiniPlayerFragmentViewModel @Inject constructor(
    private val musicPrefsUseCase: MusicPreferencesGateway

) : ViewModel() {

    private var showTimeLeft = false
    private var currentDuration = 0L

    private val progressPublisher = liveDataOf<Int>()
    private val skipToNextLiveData = liveDataOf<Boolean>()
    private val skipToPreviousLiveData = liveDataOf<Boolean>()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            musicPrefsUseCase.observeSkipToNextVisibility()
                .distinctUntilChanged()
                .collect { skipToNextLiveData.postValue(it) }
        }
        viewModelScope.launch(Dispatchers.Default) {
            musicPrefsUseCase.observeSkipToPreviousVisibility()
                .distinctUntilChanged()
                .collect { skipToPreviousLiveData.postValue(it) }
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

    val skipToNextVisibility: LiveData<Boolean> = skipToNextLiveData

    val skipToPreviousVisibility: LiveData<Boolean> = skipToPreviousLiveData

    fun getMetadata() = musicPrefsUseCase.getLastMetadata()

    fun startShowingLeftTime(show: Boolean, duration: Long) {
        showTimeLeft = show
        currentDuration = duration
    }

    val observeProgress: LiveData<Long> = progressPublisher
        .filter { showTimeLeft }
        .map { currentDuration - progressPublisher.value!! }
        .map { TimeUnit.MILLISECONDS.toMinutes(it) }

    fun updateProgress(progress: Long) {
        progressPublisher.postValue(progress.toInt())
    }

}