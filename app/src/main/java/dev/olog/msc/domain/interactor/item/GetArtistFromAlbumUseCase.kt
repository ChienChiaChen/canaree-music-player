package dev.olog.msc.domain.interactor.item

import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.core.gateway.AlbumGateway
import dev.olog.msc.core.gateway.ArtistGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCaseWithParam
import dev.olog.msc.core.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class GetArtistFromAlbumUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: ArtistGateway,
        private val albumGateway: AlbumGateway

) : ObservableUseCaseWithParam<Artist, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<Artist> {
        val albumId = mediaId.categoryId

        return albumGateway.getByParam(albumId)
                .flatMap { album -> gateway.getByParam(album.artistId) }
    }
}
