package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.msc.core.interactor.base.GetGroupUseCase
import javax.inject.Inject

class GetAllPodcastAlbumsUseCase @Inject constructor(
    gateway: PodcastAlbumGateway,
    schedulers: IoDispatcher
) : GetGroupUseCase<PodcastAlbum>(gateway, schedulers)