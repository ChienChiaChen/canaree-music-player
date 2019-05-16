package dev.olog.msc.core.interactor.item

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.entity.ItemRequest
import dev.olog.msc.core.entity.podcast.PodcastPlaylist
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetPodcastPlaylistUseCase @Inject internal constructor(
    private val schedulers: IoDispatcher,
    private val gateway: PodcastPlaylistGateway

) {

    suspend fun execute(mediaId: MediaId): ItemRequest<PodcastPlaylist> = withContext(schedulers.worker) {
        gateway.getByParam(mediaId.categoryId)
    }
}