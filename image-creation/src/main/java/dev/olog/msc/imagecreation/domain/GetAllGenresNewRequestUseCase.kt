package dev.olog.msc.imagecreation.domain

import dev.olog.msc.core.entity.track.Genre
import dev.olog.msc.core.executors.ComputationScheduler
import dev.olog.msc.core.gateway.GenreGateway
import dev.olog.msc.core.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

internal class GetAllGenresNewRequestUseCase @Inject constructor(
        schedulers: ComputationScheduler,
        private val gateway: GenreGateway

) : ObservableUseCase<List<Genre>>(schedulers) {

    override fun buildUseCaseObservable(): Observable<List<Genre>> {
        return gateway.getAllNewRequest()
    }
}