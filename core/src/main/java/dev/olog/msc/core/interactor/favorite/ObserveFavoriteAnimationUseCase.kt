package dev.olog.msc.core.interactor.favorite

import dev.olog.msc.core.entity.favorite.FavoriteEnum
import dev.olog.msc.core.executors.ComputationDispatcher
import dev.olog.msc.core.gateway.FavoriteGateway
import dev.olog.msc.core.interactor.base.ObservableFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoriteAnimationUseCase @Inject constructor(
    scheduler: ComputationDispatcher,
    private val gateway: FavoriteGateway

) : ObservableFlow<FavoriteEnum>(scheduler) {

    override suspend fun buildUseCaseObservable(): Flow<FavoriteEnum> {
        return gateway.observeToggleFavorite()
    }
}