package dev.olog.msc.core.interactor.played

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.executors.IoDispatcher
import dev.olog.msc.core.gateway.podcast.PodcastArtistGateway
import dev.olog.msc.core.gateway.track.ArtistGateway
import dev.olog.msc.core.interactor.base.CompletableFlowWithParam
import javax.inject.Inject

class InsertLastPlayedArtistUseCase @Inject constructor(
    schedulers: IoDispatcher,
    private val artistGateway: ArtistGateway,
    private val podcastGateway: PodcastArtistGateway

) : CompletableFlowWithParam<MediaId>(schedulers) {

    override suspend fun buildUseCaseObservable(mediaId: MediaId) {
        if (mediaId.isPodcastArtist) {
            return podcastGateway.addLastPlayed(mediaId.categoryValue.toLong())
        }
        return artistGateway.addLastPlayed(mediaId.categoryValue.toLong())
    }
}