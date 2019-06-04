package dev.olog.msc.core.interactor.item

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.data.request.ItemRequest
import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.executors.ComputationDispatcher
import dev.olog.msc.core.gateway.podcast.PodcastAlbumGateway
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetPodcastAlbumUseCase @Inject internal constructor(
    private val schedulers: ComputationDispatcher,
    private val gateway: PodcastAlbumGateway

) {

    suspend fun execute(mediaId: MediaId): ItemRequest<PodcastAlbum> = withContext(schedulers.worker) {
        gateway.getByParam(mediaId.categoryId)
    }
}