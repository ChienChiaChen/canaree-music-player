package dev.olog.msc.core.interactor.played

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.executors.ComputationDispatcher
import dev.olog.msc.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.msc.core.gateway.track.AlbumGateway
import dev.olog.msc.core.interactor.base.CompletableFlowWithParam
import javax.inject.Inject

class InsertLastPlayedAlbumUseCase @Inject constructor(
    schedulers: ComputationDispatcher,
    private val albumGateway: AlbumGateway,
    private val podcastGateway: PodcastAlbumGateway

) : CompletableFlowWithParam<MediaId>(schedulers) {

    override suspend fun buildUseCaseObservable(mediaId: MediaId) {
        if (mediaId.isPodcastAlbum) {
            return podcastGateway.addLastPlayed(mediaId.categoryValue.toLong())
        }
        return albumGateway.addLastPlayed(mediaId.categoryValue.toLong())
    }
}