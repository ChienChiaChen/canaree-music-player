package dev.olog.msc.presentation.dialogs.favorite

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.favorite.FavoriteType
import io.reactivex.Completable
import javax.inject.Inject

class AddFavoriteDialogPresenter @Inject constructor(
        private val mediaId: MediaId,
        private val addToFavoriteUseCase: AddToFavoriteUseCase
) {

    fun execute(): Completable {
        val type = if (mediaId.isAnyPodcast) FavoriteType.PODCAST else FavoriteType.TRACK
        return addToFavoriteUseCase.execute(AddToFavoriteUseCase.Input(mediaId, type))
    }

}