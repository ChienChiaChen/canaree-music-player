package dev.olog.msc.core.interactor.item

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.coroutines.ObservableFlowWithParam
import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.gateway.PodcastAlbumGateway
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPodcastAlbumUseCase @Inject internal constructor(
    schedulers: IoDispatcher,
    private val gateway: PodcastAlbumGateway

) : ObservableFlowWithParam<PodcastAlbum, MediaId>(schedulers) {

    override suspend fun buildUseCaseObservable(mediaId: MediaId): Flow<PodcastAlbum> {
        return gateway.getByParam(mediaId.categoryId)
    }
}