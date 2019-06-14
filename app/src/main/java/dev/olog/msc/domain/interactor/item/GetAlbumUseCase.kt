package dev.olog.msc.domain.interactor.item

import dev.olog.msc.core.entity.Album
import dev.olog.msc.core.executor.IoScheduler
import dev.olog.msc.core.gateway.AlbumGateway
import dev.olog.msc.core.interactor.base.ObservableUseCaseWithParam
import dev.olog.msc.core.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class GetAlbumUseCase @Inject internal constructor(
    schedulers: IoScheduler,
    private val gateway: AlbumGateway

) : ObservableUseCaseWithParam<Album, MediaId>(schedulers) {


    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<Album> {
        return gateway.getByParam(mediaId.categoryId)
    }
}
