package dev.olog.msc.domain.interactor.all.newrequest

import dev.olog.msc.core.entity.track.Album
import dev.olog.msc.core.executors.ComputationScheduler
import dev.olog.msc.core.gateway.AlbumGateway
import dev.olog.msc.core.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class GetAllAlbumsNewRequestUseCase @Inject constructor(
        schedulers: ComputationScheduler,
        private val gateway: AlbumGateway

) : ObservableUseCase<List<Album>>(schedulers) {

    override fun buildUseCaseObservable(): Observable<List<Album>> {
        return gateway.getAllNewRequest()
    }
}