package dev.olog.msc.core.interactor.item

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.coroutines.ObservableFlowWithParam
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.gateway.PodcastGateway
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPodcastUseCase @Inject internal constructor(
    schedulers: IoDispatcher,
    private val gateway: PodcastGateway

) : ObservableFlowWithParam<Podcast, MediaId>(schedulers) {

    override suspend fun buildUseCaseObservable(mediaId: MediaId): Flow<Podcast> {
        return gateway.getByParam(mediaId.resolveId)
    }
}
