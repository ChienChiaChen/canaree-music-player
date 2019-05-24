package dev.olog.msc.presentation.dialogs.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.msc.core.MediaId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import javax.inject.Inject

class ClearPlaylistDialogViewModel @Inject constructor(
        private val useCase: ClearPlaylistUseCase

) : ViewModel() {

    fun executeAsync(mediaId: MediaId) = viewModelScope.async(Dispatchers.Default) {
        useCase.execute(mediaId)
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

}