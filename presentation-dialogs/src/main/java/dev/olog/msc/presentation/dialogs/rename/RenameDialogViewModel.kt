package dev.olog.msc.presentation.dialogs.rename

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.msc.core.MediaId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import javax.inject.Inject

class RenameDialogViewModel @Inject constructor(
        private val renameUseCase: RenameUseCase

) : ViewModel() {

    fun executeAsync(mediaId: MediaId, newTitle: String) = viewModelScope.async(Dispatchers.Default) {
        renameUseCase.execute(Pair(mediaId, newTitle))
    }


    override fun onCleared() {
        viewModelScope.cancel()
    }

}