package dev.olog.msc.domain.interactor.all

import dev.olog.msc.core.entity.Song
import dev.olog.msc.core.executor.ComputationScheduler
import dev.olog.msc.core.gateway.SongGateway
import dev.olog.msc.domain.interactor.base.GetGroupUseCase
import javax.inject.Inject

class GetAllSongsUseCase @Inject constructor(
    gateway: SongGateway,
    schedulers: ComputationScheduler
) : GetGroupUseCase<Song>(gateway, schedulers)