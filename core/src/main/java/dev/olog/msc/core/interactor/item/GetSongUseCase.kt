package dev.olog.msc.core.interactor.item

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.entity.ItemRequest
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.track.SongGateway
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetSongUseCase @Inject internal constructor(
    private val schedulers: IoDispatcher,
    private val gateway: SongGateway

) {

    suspend fun execute(mediaId: MediaId): ItemRequest<Song> = withContext(schedulers.worker) {
        gateway.getByParam(mediaId.categoryId)
    }
}