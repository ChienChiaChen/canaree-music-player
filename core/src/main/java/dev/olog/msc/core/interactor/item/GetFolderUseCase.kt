package dev.olog.msc.core.interactor.item

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.entity.data.request.ItemRequest
import dev.olog.msc.core.entity.track.Folder
import dev.olog.msc.core.gateway.track.FolderGateway
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetFolderUseCase @Inject internal constructor(
    private val schedulers: IoDispatcher,
    private val gateway: FolderGateway

) {

    suspend fun execute(mediaId: MediaId): ItemRequest<Folder> = withContext(schedulers.worker) {
        gateway.getByParam(mediaId.categoryValue)
    }
}
