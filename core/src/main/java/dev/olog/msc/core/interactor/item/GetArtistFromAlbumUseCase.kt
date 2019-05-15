package dev.olog.msc.core.interactor.item

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.coroutines.ObservableFlowWithParam
import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.gateway.track.AlbumGateway
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArtistFromAlbumUseCase @Inject internal constructor(
    schedulers: IoDispatcher,
    private val gateway: AlbumGateway

) : ObservableFlowWithParam<Artist, MediaId>(schedulers) {

    override suspend fun buildUseCaseObservable(mediaId: MediaId): Flow<Artist> {
        return gateway.observeArtistByAlbumId(mediaId.categoryId)
    }
}
