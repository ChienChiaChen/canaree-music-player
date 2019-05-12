package dev.olog.msc.core.interactor.item

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.coroutines.ObservableFlowWithParam
import dev.olog.msc.core.entity.podcast.PodcastArtist
import dev.olog.msc.core.gateway.PodcastArtistGateway
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPodcastArtistUseCase @Inject internal constructor(
    schedulers: IoDispatcher,
    private val gateway: PodcastArtistGateway

) : ObservableFlowWithParam<PodcastArtist, MediaId>(schedulers) {

    override suspend fun buildUseCaseObservable(mediaId: MediaId): Flow<PodcastArtist> {
        return gateway.getByParam(mediaId.categoryId)
    }
}