package dev.olog.msc.core.interactor.item

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.coroutines.ObservableFlowWithParam
import dev.olog.msc.core.entity.track.Playlist
import dev.olog.msc.core.gateway.track.PlaylistGateway
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlaylistUseCase @Inject internal constructor(
    schedulers: IoDispatcher,
    private val gateway: PlaylistGateway

) : ObservableFlowWithParam<Playlist, MediaId>(schedulers) {

    override suspend fun buildUseCaseObservable(mediaId: MediaId): Flow<Playlist> {
        return gateway.observeByParam(mediaId.categoryId)
    }
}
