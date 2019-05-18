package dev.olog.msc.presentation.dialogs.delete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.msc.core.MediaId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeleteDialogViewModel @Inject constructor(
    private val deleteUseCase: DeleteUseCase
) : ViewModel() {


    fun execute(mediaId: MediaId) = viewModelScope.launch(Dispatchers.Default) {
        deleteUseCase.execute(mediaId)
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

}