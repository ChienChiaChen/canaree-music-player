package dev.olog.msc.core.interactor.item

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.coroutines.ObservableFlowWithParam
import dev.olog.msc.core.entity.podcast.PodcastPlaylist
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPodcastPlaylistUseCase @Inject internal constructor(
    schedulers: IoDispatcher,
    private val gateway: PodcastPlaylistGateway

) : ObservableFlowWithParam<PodcastPlaylist, MediaId>(schedulers) {

    override suspend fun buildUseCaseObservable(mediaId: MediaId): Flow<PodcastPlaylist> {
        return gateway.observeByParam(mediaId.categoryId)
    }
}