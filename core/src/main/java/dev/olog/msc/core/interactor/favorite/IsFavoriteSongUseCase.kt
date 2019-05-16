package dev.olog.msc.core.interactor.favorite

import dev.olog.msc.core.coroutines.ComputationDispatcher
import dev.olog.msc.core.coroutines.SingleFlowWithParam
import dev.olog.msc.core.entity.favorite.FavoriteType
import dev.olog.msc.core.gateway.FavoriteGateway
import javax.inject.Inject

class IsFavoriteSongUseCase @Inject constructor(
    schedulers: ComputationDispatcher,
    private val gateway: FavoriteGateway

) : SingleFlowWithParam<Boolean, IsFavoriteSongUseCase.Input>(schedulers) {

    override suspend fun buildUseCaseObservable(param: Input): Boolean {
        return gateway.isFavorite(param.type, param.songId)
    }

    class Input(
        val songId: Long,
        val type: FavoriteType
    )
}
