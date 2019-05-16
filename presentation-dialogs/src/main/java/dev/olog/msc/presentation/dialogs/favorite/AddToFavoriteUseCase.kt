package dev.olog.msc.presentation.dialogs.favorite

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.CompletableFlowWithParam
import dev.olog.msc.core.coroutines.ComputationDispatcher
import dev.olog.msc.core.entity.favorite.FavoriteType
import dev.olog.msc.core.gateway.FavoriteGateway
import javax.inject.Inject

class AddToFavoriteUseCase @Inject constructor(
    scheduler: ComputationDispatcher,
    private val favoriteGateway: FavoriteGateway

) : CompletableFlowWithParam<AddToFavoriteUseCase.Input>(scheduler) {

    override suspend fun buildUseCaseObservable(param: Input) {
        val mediaId = param.mediaId
        val type = param.type
        if (mediaId.isLeaf) {
            val songId = mediaId.leaf!!
            return favoriteGateway.addSingle(type, songId)
        }
        return TODO("disable add group to favorite?")
//        return getSongListByParamUseCase.execute(mediaId)
//            .firstOrError()
//            .mapToList { it.id }
//            .flatMapCompletable { favoriteGateway.addGroup(type, it) }
    }

    class Input(
        val mediaId: MediaId,
        val type: FavoriteType
    )

}