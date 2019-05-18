package dev.olog.msc.core.interactor.favorite

import dev.olog.msc.core.coroutines.ComputationDispatcher
import dev.olog.msc.core.coroutines.ObservableFlow
import dev.olog.msc.core.entity.favorite.FavoriteEnum
import dev.olog.msc.core.gateway.FavoriteGateway
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