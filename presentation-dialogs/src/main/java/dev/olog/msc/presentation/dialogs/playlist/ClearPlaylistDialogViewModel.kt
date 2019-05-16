package dev.olog.msc.presentation.dialogs.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.msc.core.MediaId
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class ClearPlaylistDialogViewModel @Inject constructor(
    private val useCase: ClearPlaylistUseCase

) : ViewModel() {

    fun execute(mediaId: MediaId) = viewModelScope.launch {
        useCase.execute(mediaId)
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

}