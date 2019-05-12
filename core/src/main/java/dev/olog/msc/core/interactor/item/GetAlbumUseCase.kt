package dev.olog.msc.core.interactor.item

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.coroutines.ObservableFlowWithParam
import dev.olog.msc.core.entity.track.Album
import dev.olog.msc.core.gateway.AlbumGateway
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlbumUseCase @Inject internal constructor(
    schedulers: IoDispatcher,
    private val gateway: AlbumGateway

) : ObservableFlowWithParam<Album, MediaId>(schedulers) {


    override suspend fun buildUseCaseObservable(mediaId: MediaId): Flow<Album> {
        return gateway.getByParam(mediaId.categoryId)
    }
}
