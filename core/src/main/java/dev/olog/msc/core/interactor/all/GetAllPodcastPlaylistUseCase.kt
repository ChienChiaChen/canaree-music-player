package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.entity.podcast.PodcastPlaylist
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.msc.core.interactor.base.GetGroupUseCase
import javax.inject.Inject

class GetAllPodcastPlaylistUseCase @Inject constructor(
    gateway: PodcastPlaylistGateway,
    schedulers: IoDispatcher
) : GetGroupUseCase<PodcastPlaylist>(gateway, schedulers)