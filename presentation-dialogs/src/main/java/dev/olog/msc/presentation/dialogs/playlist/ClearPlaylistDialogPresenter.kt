package dev.olog.msc.presentation.dialogs.playlist

import dev.olog.msc.core.MediaId
import io.reactivex.Completable
import javax.inject.Inject

class ClearPlaylistDialogPresenter @Inject constructor(
        private val mediaId: MediaId,
        private val useCase: ClearPlaylistUseCase

) {

    fun execute(): Completable {
        return useCase.execute(mediaId)
    }

}