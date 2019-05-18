package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.coroutines.GetGroupUseCase
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.entity.podcast.PodcastArtist
import dev.olog.msc.core.gateway.podcast.PodcastArtistGateway
import javax.inject.Inject

class ObserveAllPodcastArtistsUseCase @Inject constructor(
    gateway: PodcastArtistGateway,
    schedulers: IoDispatcher
) : GetGroupUseCase<PodcastArtist>(gateway, schedulers)