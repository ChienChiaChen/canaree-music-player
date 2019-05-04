package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.executors.ComputationScheduler
import dev.olog.msc.core.gateway.PodcastGateway
import dev.olog.msc.core.interactor.base.GetGroupUseCase
import javax.inject.Inject

class GetAllPodcastUseCase @Inject constructor(
        gateway: PodcastGateway,
        schedulers: ComputationScheduler
) : GetGroupUseCase<Podcast>(gateway, schedulers)