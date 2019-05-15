package dev.olog.msc.core.interactor.item

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.coroutines.ObservableFlowWithParam
import dev.olog.msc.core.entity.track.Folder
import dev.olog.msc.core.gateway.track.FolderGateway
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFolderUseCase @Inject internal constructor(
    schedulers: IoDispatcher,
    private val gateway: FolderGateway

) : ObservableFlowWithParam<Folder, MediaId>(schedulers) {

    override suspend fun buildUseCaseObservable(mediaId: MediaId): Flow<Folder> {
        return gateway.observeByParam(mediaId.categoryValue)
    }
}
