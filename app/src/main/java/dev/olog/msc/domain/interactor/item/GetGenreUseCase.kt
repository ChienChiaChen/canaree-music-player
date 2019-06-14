package dev.olog.msc.domain.interactor.item

import dev.olog.msc.core.entity.Genre
import dev.olog.msc.core.executor.IoScheduler
import dev.olog.msc.core.gateway.GenreGateway
import dev.olog.msc.core.interactor.base.ObservableUseCaseWithParam
import dev.olog.msc.core.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class GetGenreUseCase @Inject internal constructor(
    schedulers: IoScheduler,
    private val gateway: GenreGateway

) : ObservableUseCaseWithParam<Genre, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<Genre> {
        return gateway.getByParam(mediaId.categoryId)
    }
}
