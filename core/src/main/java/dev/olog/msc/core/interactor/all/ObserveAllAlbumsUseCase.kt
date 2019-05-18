package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.entity.track.Album
import dev.olog.msc.core.gateway.track.AlbumGateway
import dev.olog.msc.core.interactor.base.GetGroupUseCase
import javax.inject.Inject

class ObserveAllAlbumsUseCase @Inject constructor(
    gateway: AlbumGateway,
    schedulers: IoDispatcher
) : GetGroupUseCase<Album>(gateway, schedulers)