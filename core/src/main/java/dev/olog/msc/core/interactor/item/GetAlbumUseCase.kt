package dev.olog.msc.core.interactor.item

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.data.request.ItemRequest
import dev.olog.msc.core.entity.track.Album
import dev.olog.msc.core.executors.IoDispatcher
import dev.olog.msc.core.gateway.track.AlbumGateway
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAlbumUseCase @Inject internal constructor(
    private val schedulers: IoDispatcher,
    private val gateway: AlbumGateway

) {


    suspend fun execute(mediaId: MediaId): ItemRequest<Album> = withContext(schedulers.worker) {
        gateway.getByParam(mediaId.categoryId)
    }
}
