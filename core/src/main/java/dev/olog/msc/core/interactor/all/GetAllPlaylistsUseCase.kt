package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.entity.track.Playlist
import dev.olog.msc.core.executors.ComputationScheduler
import dev.olog.msc.core.gateway.PlaylistGateway
import dev.olog.msc.core.interactor.base.GetGroupUseCase
import javax.inject.Inject

class GetAllPlaylistsUseCase @Inject constructor(
        gateway: PlaylistGateway,
        schedulers: ComputationScheduler
) : GetGroupUseCase<Playlist>(gateway, schedulers)