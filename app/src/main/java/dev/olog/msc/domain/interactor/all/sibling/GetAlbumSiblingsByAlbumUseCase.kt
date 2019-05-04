package dev.olog.msc.domain.interactor.all.sibling

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.track.Album
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.AlbumGateway
import dev.olog.msc.core.interactor.base.ObservableUseCaseWithParam
import dev.olog.msc.core.interactor.item.GetAlbumUseCase
import io.reactivex.Observable
import javax.inject.Inject


class GetAlbumSiblingsByAlbumUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val getAlbumUseCase: GetAlbumUseCase,
        private val albumGateway: AlbumGateway

) : ObservableUseCaseWithParam<List<Album>, MediaId>(schedulers) {


    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<Album>> {
        val albumId = mediaId.categoryValue.toLong()
        return getAlbumUseCase.execute(mediaId)
                .map { it.artistId }
                .flatMap { artistId ->
                    albumGateway.observeByArtist(artistId)
                            .map { it.filter { it.id != albumId } }
                }
    }

}
