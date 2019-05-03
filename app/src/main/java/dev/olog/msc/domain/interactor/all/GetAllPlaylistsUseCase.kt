package dev.olog.msc.domain.interactor.all

import dev.olog.msc.core.entity.track.Playlist
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.core.gateway.PlaylistGateway
import dev.olog.msc.domain.interactor.base.GetGroupUseCase
import javax.inject.Inject

class GetAllPlaylistsUseCase @Inject constructor(
        gateway: PlaylistGateway,
        schedulers: ComputationScheduler
) : GetGroupUseCase<Playlist>(gateway, schedulers)