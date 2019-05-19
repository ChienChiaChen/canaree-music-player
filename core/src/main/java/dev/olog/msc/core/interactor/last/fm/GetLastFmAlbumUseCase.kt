package dev.olog.msc.core.interactor.last.fm

import dev.olog.msc.core.coroutines.ComputationDispatcher
import dev.olog.msc.core.coroutines.SingleFlowWithParam
import dev.olog.msc.core.entity.LastFmAlbum
import dev.olog.msc.core.gateway.LastFmGateway
import javax.inject.Inject

class GetLastFmAlbumUseCase @Inject constructor(
    schedulers: ComputationDispatcher,
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