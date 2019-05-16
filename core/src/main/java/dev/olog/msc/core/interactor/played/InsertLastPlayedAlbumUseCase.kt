package dev.olog.msc.core.interactor.played

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.CompletableFlowWithParam
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.msc.core.gateway.track.AlbumGateway
import javax.inject.Inject

class InsertLastPlayedAlbumUseCase @Inject constructor(
    schedulers: IoDispatcher,
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