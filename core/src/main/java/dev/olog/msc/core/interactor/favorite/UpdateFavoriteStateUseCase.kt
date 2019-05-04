package dev.olog.msc.core.interactor.favorite

import dev.olog.msc.core.entity.favorite.FavoriteStateEntity
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.FavoriteGateway
import dev.olog.msc.core.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class UpdateFavoriteStateUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val favoriteGateway: FavoriteGateway

) : CompletableUseCaseWithParam<FavoriteStateEntity>(schedulers) {

    override fun buildUseCaseObservable(param: FavoriteStateEntity): Completable {
        return Completable.fromCallable {
            favoriteGateway.updateFavoriteState(param.favoriteType, param)
        }
    }
}