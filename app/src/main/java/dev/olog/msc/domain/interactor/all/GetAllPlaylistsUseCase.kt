package dev.olog.msc.domain.interactor.all

import dev.olog.msc.core.entity.Playlist
import dev.olog.msc.core.executor.ComputationScheduler
import dev.olog.msc.core.gateway.PlaylistGateway
import dev.olog.msc.domain.interactor.base.GetGroupUseCase
import javax.inject.Inject

class GetAllPlaylistsUseCase @Inject constructor(
    gateway: PlaylistGateway,
    schedulers: ComputationScheduler
) : GetGroupUseCase<Playlist>(gateway, schedulers)