package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.executors.ComputationScheduler
import dev.olog.msc.core.gateway.PodcastAlbumGateway
import dev.olog.msc.core.interactor.base.GetGroupUseCase
import javax.inject.Inject

class GetAllPodcastAlbumsUseCase @Inject constructor(
        gateway: PodcastAlbumGateway,
        schedulers: ComputationScheduler
) : GetGroupUseCase<PodcastAlbum>(gateway, schedulers)