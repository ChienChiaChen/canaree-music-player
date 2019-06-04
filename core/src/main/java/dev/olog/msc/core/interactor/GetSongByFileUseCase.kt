package dev.olog.msc.core.interactor

import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.executors.ComputationDispatcher
import dev.olog.msc.core.gateway.track.SongGateway
import dev.olog.msc.core.interactor.base.SingleFlowWithParam
import javax.inject.Inject

class GetSongByFileUseCase @Inject internal constructor(
    schedulers: ComputationDispatcher,
    private val gateway: SongGateway

) : SingleFlowWithParam<Song?, String>(schedulers) {

    override suspend fun buildUseCaseObservable(param: String): Song? {
        return gateway.getByUri(param)
    }
}
