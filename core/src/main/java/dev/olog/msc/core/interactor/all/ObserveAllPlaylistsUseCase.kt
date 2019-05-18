package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.coroutines.GetGroupUseCase
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.entity.track.Playlist
import dev.olog.msc.core.gateway.track.PlaylistGateway
import javax.inject.Inject

class ObserveAllPlaylistsUseCase @Inject constructor(
    gateway: PlaylistGateway,
    schedulers: IoDispatcher
) : GetGroupUseCase<Playlist>(gateway, schedulers)