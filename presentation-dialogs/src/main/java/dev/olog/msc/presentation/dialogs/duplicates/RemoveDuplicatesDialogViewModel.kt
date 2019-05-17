package dev.olog.msc.presentation.dialogs.duplicates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.msc.core.MediaId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class RemoveDuplicatesDialogViewModel @Inject constructor(
    private val useCase: RemoveDuplicatesUseCase
) : ViewModel() {

    fun execute(mediaId: MediaId) = viewModelScope.launch(Dispatchers.Default) {
        useCase.execute(mediaId)
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

}