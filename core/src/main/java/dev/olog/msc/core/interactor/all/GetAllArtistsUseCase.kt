package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.executors.ComputationScheduler
import dev.olog.msc.core.gateway.ArtistGateway
import dev.olog.msc.core.interactor.base.GetGroupUseCase
import javax.inject.Inject

class GetAllArtistsUseCase @Inject constructor(
        gateway: ArtistGateway,
        schedulers: ComputationScheduler
) : GetGroupUseCase<Artist>(gateway, schedulers)