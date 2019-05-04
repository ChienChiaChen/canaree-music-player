package dev.olog.msc.core.interactor.favorite

import dev.olog.msc.core.gateway.FavoriteGateway
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
        private val gateway: FavoriteGateway

) {

    fun execute() {
        gateway.toggleFavorite()
    }
}

