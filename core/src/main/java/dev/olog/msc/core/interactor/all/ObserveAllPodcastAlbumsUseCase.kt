package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.coroutines.GetGroupUseCase
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.gateway.podcast.PodcastAlbumGateway
import javax.inject.Inject

class ObserveAllPodcastAlbumsUseCase @Inject constructor(
    gateway: PodcastAlbumGateway,
    schedulers: IoDispatcher
) : GetGroupUseCase<PodcastAlbum>(gateway, schedulers)