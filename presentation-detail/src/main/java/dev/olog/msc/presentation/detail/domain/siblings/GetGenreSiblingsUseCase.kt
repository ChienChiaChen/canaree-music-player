package dev.olog.msc.presentation.detail.domain.siblings

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.track.Genre
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.GenreGateway
import dev.olog.msc.core.interactor.base.ObservableUseCaseWithParam
import io.reactivex.Observable
import javax.inject.Inject

class GetGenreSiblingsUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: GenreGateway

) : ObservableUseCaseWithParam<List<Genre>, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId) : Observable<List<Genre>> {
        val genreId = mediaId.categoryValue.toLong()

        return gateway.getAll().map { it.filter { it.id != genreId } }
    }
}
