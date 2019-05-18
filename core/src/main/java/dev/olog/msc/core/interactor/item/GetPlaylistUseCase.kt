package dev.olog.msc.core.interactor.item

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.entity.data.request.ItemRequest
import dev.olog.msc.core.entity.track.Playlist
import dev.olog.msc.core.gateway.track.PlaylistGateway
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetPlaylistUseCase @Inject internal constructor(
    private val schedulers: IoDispatcher,
    private val gateway: PlaylistGateway

) {

    suspend fun execute(mediaId: MediaId): ItemRequest<Playlist> = withContext(schedulers.worker) {
        gateway.getByParam(mediaId.categoryId)
    }
}