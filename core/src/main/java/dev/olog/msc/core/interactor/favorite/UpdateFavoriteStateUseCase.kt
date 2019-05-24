package dev.olog.msc.core.interactor.favorite

import dev.olog.msc.core.entity.favorite.FavoriteStateEntity
import dev.olog.msc.core.executors.ComputationDispatcher
import dev.olog.msc.core.gateway.FavoriteGateway
import dev.olog.msc.core.interactor.base.CompletableFlowWithParam
import javax.inject.Inject

class UpdateFavoriteStateUseCase @Inject constructor(
    schedulers: ComputationDispatcher,
    private val favoriteGateway: FavoriteGateway

) : CompletableFlowWithParam<FavoriteStateEntity>(schedulers) {

    override suspend fun buildUseCaseObservable(param: FavoriteStateEntity) {
        favoriteGateway.updateFavoriteState(param.favoriteType, param)
    }
}