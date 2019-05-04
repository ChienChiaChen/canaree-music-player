package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.entity.track.Folder
import dev.olog.msc.core.executors.ComputationScheduler
import dev.olog.msc.core.gateway.FolderGateway
import dev.olog.msc.core.interactor.base.GetGroupUseCase
import javax.inject.Inject

class GetAllFoldersUseCase @Inject constructor(
        gateway: FolderGateway,
        schedulers: ComputationScheduler
) : GetGroupUseCase<Folder>(gateway, schedulers)