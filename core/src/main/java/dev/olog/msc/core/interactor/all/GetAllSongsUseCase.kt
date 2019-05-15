package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.track.SongGateway
import dev.olog.msc.core.interactor.base.GetGroupUseCase
import javax.inject.Inject

class GetAllSongsUseCase @Inject constructor(
    gateway: SongGateway,
    schedulers: IoDispatcher
) : GetGroupUseCase<Song>(gateway, schedulers)