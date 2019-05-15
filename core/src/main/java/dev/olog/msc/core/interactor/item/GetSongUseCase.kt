package dev.olog.msc.core.interactor.item

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.coroutines.ObservableFlowWithParam
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.track.SongGateway
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSongUseCase @Inject internal constructor(
    schedulers: IoDispatcher,
    private val gateway: SongGateway

) : ObservableFlowWithParam<Song, MediaId>(schedulers) {

    override suspend fun buildUseCaseObservable(mediaId: MediaId): Flow<Song> {
        return gateway.observeByParam(mediaId.resolveId)
    }
}
