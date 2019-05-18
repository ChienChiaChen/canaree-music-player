package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.coroutines.GetGroupUseCase
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.entity.podcast.PodcastPlaylist
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import javax.inject.Inject

class ObserveAllPodcastPlaylistUseCase @Inject constructor(
    gateway: PodcastPlaylistGateway,
    schedulers: IoDispatcher
) : GetGroupUseCase<PodcastPlaylist>(gateway, schedulers)