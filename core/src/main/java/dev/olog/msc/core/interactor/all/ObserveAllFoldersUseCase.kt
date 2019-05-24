package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.entity.track.Folder
import dev.olog.msc.core.executors.IoDispatcher
import dev.olog.msc.core.gateway.track.FolderGateway
import dev.olog.msc.core.interactor.base.GetGroupUseCase
import javax.inject.Inject

class ObserveAllFoldersUseCase @Inject constructor(
    gateway: FolderGateway,
    schedulers: IoDispatcher
) : GetGroupUseCase<Folder>(gateway, schedulers)