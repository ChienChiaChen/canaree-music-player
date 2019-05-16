package dev.olog.msc.core.interactor

import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.coroutines.SingleFlowWithParam
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.track.SongGateway
import javax.inject.Inject

class GetSongByFileUseCase @Inject internal constructor(
        schedulers: IoDispatcher,
        private val gateway: SongGateway

) : SingleFlowWithParam<Song?, String>(schedulers) {

    override suspend fun buildUseCaseObservable(param: String): Song? {
        return gateway.getByUri(param)
    }
}
