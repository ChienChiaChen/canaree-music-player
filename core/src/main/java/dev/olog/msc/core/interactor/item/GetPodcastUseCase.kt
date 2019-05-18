package dev.olog.msc.core.interactor.item

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.entity.data.request.ItemRequest
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.gateway.podcast.PodcastGateway
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetPodcastUseCase @Inject internal constructor(
    private val schedulers: IoDispatcher,
    private val gateway: PodcastGateway

) {

    suspend fun execute(mediaId: MediaId): ItemRequest<Podcast> = withContext(schedulers.worker) {
        gateway.getByParam(mediaId.categoryId)
    }
}