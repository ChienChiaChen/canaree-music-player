package dev.olog.msc.presentation.detail.domain

import dev.olog.msc.core.entity.PlaylistType
import dev.olog.msc.core.gateway.track.PlaylistGateway
import javax.inject.Inject

class MoveItemInPlaylistUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway
) {

    fun execute(input: Input): Boolean{
        val (playlistId, from, to, type) = input
        if (type == PlaylistType.PODCAST){
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