package dev.olog.msc.presentation.dialogs.delete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.msc.core.MediaId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import javax.inject.Inject

class DeleteDialogViewModel @Inject constructor(
        private val deleteUseCase: DeleteUseCase
) : ViewModel() {


    fun executeAsync(mediaId: MediaId) = viewModelScope.async(Dispatchers.Default) {
        deleteUseCase.execute(mediaId)
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

}