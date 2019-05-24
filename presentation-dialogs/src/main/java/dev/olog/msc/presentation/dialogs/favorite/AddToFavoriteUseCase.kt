package dev.olog.msc.presentation.dialogs.favorite

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.favorite.FavoriteType
import dev.olog.msc.core.executors.ComputationDispatcher
import dev.olog.msc.core.gateway.FavoriteGateway
import dev.olog.msc.core.interactor.base.CompletableFlowWithParam
import javax.inject.Inject

class AddToFavoriteUseCase @Inject constructor(
    scheduler: ComputationDispatcher,
    private val favoriteGateway: FavoriteGateway

) : CompletableFlowWithParam<AddToFavoriteUseCase.Input>(scheduler) {

    override suspend fun buildUseCaseObservable(param: Input) {
        val mediaId = param.mediaId
        val type = param.type
        val songId = mediaId.leaf!!
        return favoriteGateway.addSingle(type, songId)
    }

    class Input(
        val mediaId: MediaId,
        val type: FavoriteType
    )

}