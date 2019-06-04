package dev.olog.msc.core.interactor.last.fm

import dev.olog.msc.core.entity.LastFmAlbum
import dev.olog.msc.core.executors.IoDispatcher
import dev.olog.msc.core.gateway.LastFmGateway
import dev.olog.msc.core.interactor.base.SingleFlowWithParam
import javax.inject.Inject

class GetLastFmAlbumUseCase @Inject constructor(
    schedulers: IoDispatcher,
    private val gateway: LastFmGateway

) : SingleFlowWithParam<LastFmAlbum?, LastFmAlbumRequest>(schedulers) {

    override suspend fun buildUseCaseObservable(param: LastFmAlbumRequest): LastFmAlbum? {
        val (id) = param
        return gateway.getAlbum(id)
    }
}

data class LastFmAlbumRequest(
    val id: Long,
    val title: String,
    val artist: String
)