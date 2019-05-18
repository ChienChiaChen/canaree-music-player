package dev.olog.msc.core.interactor.item

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.entity.data.request.ItemRequest
import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.gateway.track.ArtistGateway
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetArtistUseCase @Inject internal constructor(
    private val schedulers: IoDispatcher,
    private val gateway: ArtistGateway

) {

    suspend fun execute(mediaId: MediaId): ItemRequest<Artist> = withContext(schedulers.worker) {
        gateway.getByParam(mediaId.categoryId)
    }
}