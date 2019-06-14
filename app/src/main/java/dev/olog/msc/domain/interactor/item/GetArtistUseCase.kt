package dev.olog.msc.domain.interactor.item

import dev.olog.msc.core.entity.Artist
import dev.olog.msc.core.executor.IoScheduler
import dev.olog.msc.core.gateway.ArtistGateway
import dev.olog.msc.core.interactor.base.ObservableUseCaseWithParam
import dev.olog.msc.core.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class GetArtistUseCase @Inject internal constructor(
    schedulers: IoScheduler,
    private val gateway: ArtistGateway

) : ObservableUseCaseWithParam<Artist, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<Artist> {
        return gateway.getByParam(mediaId.categoryId)
    }
}
