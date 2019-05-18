package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.coroutines.GetGroupUseCase
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.entity.track.Folder
import dev.olog.msc.core.gateway.track.FolderGateway
import javax.inject.Inject

class ObserveAllFoldersUseCase @Inject constructor(
    gateway: FolderGateway,
    schedulers: IoDispatcher
) : GetGroupUseCase<Folder>(gateway, schedulers)