package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.gateway.podcast.PodcastGateway
import dev.olog.msc.core.interactor.base.GetGroupUseCase
import javax.inject.Inject

class GetAllPodcastUseCase @Inject constructor(
    gateway: PodcastGateway,
    schedulers: IoDispatcher
) : GetGroupUseCase<Podcast>(gateway, schedulers)