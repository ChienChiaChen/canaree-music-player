package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.executors.ComputationScheduler
import dev.olog.msc.core.gateway.SongGateway
import dev.olog.msc.core.interactor.base.GetGroupUseCase
import javax.inject.Inject

class GetAllSongsUseCase @Inject constructor(
        gateway: SongGateway,
        schedulers: ComputationScheduler
) : GetGroupUseCase<Song>(gateway, schedulers)