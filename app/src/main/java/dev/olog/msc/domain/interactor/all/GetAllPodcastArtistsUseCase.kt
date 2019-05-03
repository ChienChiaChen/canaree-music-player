package dev.olog.msc.domain.interactor.all

import dev.olog.msc.core.entity.podcast.PodcastArtist
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.core.gateway.PodcastArtistGateway
import dev.olog.msc.domain.interactor.base.GetGroupUseCase
import javax.inject.Inject

class GetAllPodcastArtistsUseCase @Inject constructor(
        gateway: PodcastArtistGateway,
        schedulers: ComputationScheduler
) : GetGroupUseCase<PodcastArtist>(gateway, schedulers)