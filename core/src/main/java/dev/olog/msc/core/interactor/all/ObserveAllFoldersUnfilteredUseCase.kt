package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.entity.track.Folder
import dev.olog.msc.core.executors.IoDispatcher
import dev.olog.msc.core.gateway.track.FolderGateway
import dev.olog.msc.core.interactor.base.ObservableFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAllFoldersUnfilteredUseCase @Inject constructor(
    scheduler: IoDispatcher,
    private val gateway: FolderGateway

) : ObservableFlow<List<Folder>>(scheduler) {

    override suspend fun buildUseCaseObservable(): Flow<List<Folder>> {
        return gateway.getAllUnfiltered()
    }
}