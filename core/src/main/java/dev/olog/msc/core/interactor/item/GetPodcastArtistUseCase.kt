package dev.olog.msc.core.interactor.item

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.entity.ItemRequest
import dev.olog.msc.core.entity.podcast.PodcastArtist
import dev.olog.msc.core.gateway.podcast.PodcastArtistGateway
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetPodcastArtistUseCase @Inject internal constructor(
    private val schedulers: IoDispatcher,
    private val gateway: PodcastArtistGateway

) {

    suspend fun execute(mediaId: MediaId): ItemRequest<PodcastArtist> = withContext(schedulers.worker) {
        gateway.getByParam(mediaId.categoryId)
    }
}