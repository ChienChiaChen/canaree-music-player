package dev.olog.msc.core.interactor.last.fm

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.executors.ComputationDispatcher
import dev.olog.msc.core.gateway.LastFmGateway
import dev.olog.msc.core.interactor.base.CompletableFlowWithParam
import javax.inject.Inject

class DeleteLastFmAlbumUseCase @Inject constructor(
    schedulers: ComputationDispatcher,
    private val gateway: LastFmGateway

) : CompletableFlowWithParam<MediaId>(schedulers) {

    override suspend fun buildUseCaseObservable(param: MediaId) {
        gateway.deleteAlbum(param.resolveId)
    }
}