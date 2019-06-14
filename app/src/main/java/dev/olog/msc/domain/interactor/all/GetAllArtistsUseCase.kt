package dev.olog.msc.domain.interactor.all

import dev.olog.msc.core.entity.Artist
import dev.olog.msc.core.executor.ComputationScheduler
import dev.olog.msc.core.gateway.ArtistGateway
import dev.olog.msc.domain.interactor.base.GetGroupUseCase
import javax.inject.Inject

class GetAllArtistsUseCase @Inject constructor(
    gateway: ArtistGateway,
    schedulers: ComputationScheduler
) : GetGroupUseCase<Artist>(gateway, schedulers)