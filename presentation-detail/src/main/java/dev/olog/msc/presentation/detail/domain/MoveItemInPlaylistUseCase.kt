package dev.olog.msc.presentation.detail.domain

import dev.olog.msc.core.entity.PlaylistType
import dev.olog.msc.core.executors.ComputationDispatcher
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.core.interactor.base.SingleFlowWithParam
import javax.inject.Inject

class MoveItemInPlaylistUseCase @Inject constructor(
    dispatcher: ComputationDispatcher,
    private val playlistGateway: PlaylistGateway
) : SingleFlowWithParam<Boolean, MoveItemInPlaylistUseCase.Input>(dispatcher) {

    override suspend fun buildUseCaseObservable(param: Input): Boolean {
        val (playlistId, from, to, type) = param
        if (type == PlaylistType.PODCAST) {
            throw IllegalStateException("can not move podcast playlist")
        }
        return playlistGateway.moveItem(playlistId, from, to)
    }

    data class Input(
        val playlistId: Long,
        val from: Int,
        val to: Int,
        val type: PlaylistType
    )

}