package dev.olog.msc.core.interactor.item

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.entity.ItemRequest
import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.gateway.podcast.PodcastAlbumGateway
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetPodcastAlbumUseCase @Inject internal constructor(
    private val schedulers: IoDispatcher,
    private val gateway: PodcastAlbumGateway

) {

    suspend fun execute(mediaId: MediaId): ItemRequest<PodcastAlbum> = withContext(schedulers.worker) {
        gateway.getByParam(mediaId.categoryId)
    }
}