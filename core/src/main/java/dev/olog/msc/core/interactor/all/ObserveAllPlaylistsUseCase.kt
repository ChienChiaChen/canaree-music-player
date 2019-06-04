package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.entity.track.Playlist
import dev.olog.msc.core.executors.ComputationDispatcher
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.core.interactor.base.GetGroupUseCase
import javax.inject.Inject

class ObserveAllPlaylistsUseCase @Inject constructor(
    gateway: PlaylistGateway,
    schedulers: ComputationDispatcher
) : GetGroupUseCase<Playlist>(gateway, schedulers)