package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.coroutines.GetGroupUseCase
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.gateway.track.ArtistGateway
import javax.inject.Inject

class ObserveAllArtistsUseCase @Inject constructor(
    gateway: ArtistGateway,
    schedulers: IoDispatcher
) : GetGroupUseCase<Artist>(gateway, schedulers)