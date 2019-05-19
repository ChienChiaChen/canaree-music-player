package dev.olog.msc.core.interactor.last.fm

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.CompletableFlowWithParam
import dev.olog.msc.core.coroutines.ComputationDispatcher
import dev.olog.msc.core.gateway.LastFmGateway
import javax.inject.Inject

class DeleteLastFmArtistUseCase @Inject constructor(
    schedulers: ComputationDispatcher,
    private val gateway: LastFmGateway

) : CompletableFlowWithParam<MediaId>(schedulers) {

    override suspend fun buildUseCaseObservable(param: MediaId) {
        gateway.deleteArtist(param.resolveId)
    }
}