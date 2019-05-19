package dev.olog.msc.core.interactor.last.fm

import dev.olog.msc.core.coroutines.ComputationDispatcher
import dev.olog.msc.core.coroutines.SingleFlowWithParam
import dev.olog.msc.core.entity.LastFmTrack
import dev.olog.msc.core.gateway.LastFmGateway
import javax.inject.Inject

class GetLastFmTrackUseCase @Inject constructor(
    schedulers: ComputationDispatcher,
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