package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.coroutines.GetGroupUseCase
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.track.SongGateway
import javax.inject.Inject

class ObserveAllSongsUseCase @Inject constructor(
    gateway: SongGateway,
    schedulers: IoDispatcher
) : GetGroupUseCase<Song>(gateway, schedulers)