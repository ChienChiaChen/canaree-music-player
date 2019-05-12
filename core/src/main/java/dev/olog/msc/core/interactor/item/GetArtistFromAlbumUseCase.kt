package dev.olog.msc.core.interactor.item

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.coroutines.ObservableFlowWithParam
import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.gateway.AlbumGateway
import dev.olog.msc.core.gateway.ArtistGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import javax.inject.Inject

class GetArtistFromAlbumUseCase @Inject internal constructor(
    schedulers: IoDispatcher,
    private val gateway: ArtistGateway,
    private val albumGateway: AlbumGateway

) : ObservableFlowWithParam<Artist, MediaId>(schedulers) {

    override suspend fun buildUseCaseObservable(mediaId: MediaId): Flow<Artist> {
        val albumId = mediaId.categoryId
        return albumGateway.getByParam(albumId)
            .flatMapConcat { album -> gateway.getByParam(album.artistId) }
    }
}
