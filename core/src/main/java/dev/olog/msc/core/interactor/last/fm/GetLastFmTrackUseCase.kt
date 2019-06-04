package dev.olog.msc.core.interactor.last.fm

import dev.olog.msc.core.entity.LastFmTrack
import dev.olog.msc.core.executors.IoDispatcher
import dev.olog.msc.core.gateway.LastFmGateway
import dev.olog.msc.core.interactor.base.SingleFlowWithParam
import javax.inject.Inject

class GetLastFmTrackUseCase @Inject constructor(
    schedulers: IoDispatcher,
    private val gateway: LastFmGateway

) : SingleFlowWithParam<LastFmTrack?, LastFmTrackRequest>(schedulers) {

    override suspend fun buildUseCaseObservable(param: LastFmTrackRequest): LastFmTrack? {
        val (id) = param
        return gateway.getTrack(id)
    }
}

data class LastFmTrackRequest(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String
)