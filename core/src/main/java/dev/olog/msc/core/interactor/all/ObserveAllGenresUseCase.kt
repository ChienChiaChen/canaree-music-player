package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.coroutines.GetGroupUseCase
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.entity.track.Genre
import dev.olog.msc.core.gateway.track.GenreGateway
import javax.inject.Inject

class ObserveAllGenresUseCase @Inject constructor(
    gateway: GenreGateway,
    schedulers: IoDispatcher
) : GetGroupUseCase<Genre>(gateway, schedulers)