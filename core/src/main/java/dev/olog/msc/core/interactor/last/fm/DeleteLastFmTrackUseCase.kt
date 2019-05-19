package dev.olog.msc.core.interactor.last.fm

import dev.olog.msc.core.coroutines.CompletableFlowWithParam
import dev.olog.msc.core.coroutines.ComputationDispatcher
import dev.olog.msc.core.gateway.LastFmGateway
import javax.inject.Inject

class DeleteLastFmTrackUseCase @Inject constructor(
    schedulers: ComputationDispatcher,
    private val gateway: LastFmGateway

) : CompletableFlowWithParam<Long>(schedulers) {

    override suspend fun buildUseCaseObservable(artistId: Long) {
        gateway.deleteTrack(artistId)
    }
}