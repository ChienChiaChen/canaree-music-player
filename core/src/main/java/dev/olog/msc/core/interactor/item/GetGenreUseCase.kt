package dev.olog.msc.core.interactor.item

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.entity.data.request.ItemRequest
import dev.olog.msc.core.entity.track.Genre
import dev.olog.msc.core.gateway.track.GenreGateway
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetGenreUseCase @Inject internal constructor(
    private val schedulers: IoDispatcher,
    private val gateway: GenreGateway

) {

    suspend fun execute(mediaId: MediaId): ItemRequest<Genre> = withContext(schedulers.worker) {
        gateway.getByParam(mediaId.categoryId)
    }
}
