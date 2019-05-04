package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.entity.track.Album
import dev.olog.msc.core.executors.ComputationScheduler
import dev.olog.msc.core.gateway.AlbumGateway
import dev.olog.msc.core.interactor.base.GetGroupUseCase
import javax.inject.Inject

class GetAllAlbumsUseCase @Inject constructor(
        gateway: AlbumGateway,
        schedulers: ComputationScheduler
) : GetGroupUseCase<Album>(gateway, schedulers)