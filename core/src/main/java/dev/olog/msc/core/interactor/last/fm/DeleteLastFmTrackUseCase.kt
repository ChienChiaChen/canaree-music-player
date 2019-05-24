package dev.olog.msc.core.interactor.last.fm

import dev.olog.msc.core.executors.ComputationDispatcher
import dev.olog.msc.core.gateway.LastFmGateway
import dev.olog.msc.core.interactor.base.CompletableFlowWithParam
import javax.inject.Inject

class DeleteLastFmTrackUseCase @Inject constructor(
    schedulers: ComputationDispatcher,
    private val gateway: LastFmGateway

) : CompletableFlowWithParam<Long>(schedulers) {

    override suspend fun buildUseCaseObservable(artistId: Long) {
        gateway.deleteTrack(artistId)
    }
}