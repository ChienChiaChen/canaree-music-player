package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.entity.podcast.PodcastArtist
import dev.olog.msc.core.gateway.podcast.PodcastArtistGateway
import dev.olog.msc.core.interactor.base.GetGroupUseCase
import javax.inject.Inject

class GetAllPodcastArtistsUseCase @Inject constructor(
    gateway: PodcastArtistGateway,
    schedulers: IoDispatcher
) : GetGroupUseCase<PodcastArtist>(gateway, schedulers)