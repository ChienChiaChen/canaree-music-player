package dev.olog.msc.core.interactor.item

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.ComputationDispatcher
import dev.olog.msc.core.coroutines.ObservableFlowWithParam
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.track.SongGateway
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUneditedSongUseCase @Inject internal constructor(
    schedulers: ComputationDispatcher,
    private val gateway: SongGateway

) : ObservableFlowWithParam<Song, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun buildUseCaseObservable(mediaId: MediaId): Flow<Song> {
        val songId = mediaId.leaf!!
        return gateway.getUneditedByParam(songId)
    }
}
