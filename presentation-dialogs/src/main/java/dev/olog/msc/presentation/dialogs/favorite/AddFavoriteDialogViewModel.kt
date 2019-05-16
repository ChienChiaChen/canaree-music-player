package dev.olog.msc.presentation.dialogs.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.favorite.FavoriteType
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddFavoriteDialogViewModel @Inject constructor(
    private val addToFavoriteUseCase: AddToFavoriteUseCase
) : ViewModel() {

    fun execute(mediaId: MediaId) = viewModelScope.launch {
        val type = if (mediaId.isAnyPodcast) FavoriteType.PODCAST else FavoriteType.TRACK
        addToFavoriteUseCase.execute(AddToFavoriteUseCase.Input(mediaId, type))
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

}