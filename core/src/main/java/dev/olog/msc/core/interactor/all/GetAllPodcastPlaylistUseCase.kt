package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.entity.podcast.PodcastPlaylist
import dev.olog.msc.core.executors.ComputationScheduler
import dev.olog.msc.core.gateway.PodcastPlaylistGateway
import dev.olog.msc.core.interactor.base.GetGroupUseCase
import javax.inject.Inject

class GetAllPodcastPlaylistUseCase @Inject constructor(
        gateway: PodcastPlaylistGateway,
        schedulers: ComputationScheduler
) : GetGroupUseCase<PodcastPlaylist>(gateway, schedulers)