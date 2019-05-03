package dev.olog.msc.domain.interactor.all.newrequest

import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.core.gateway.SongGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class GetAllSongsNewRequestUseCase @Inject constructor(
        private val gateway: SongGateway,
        schedulers: ComputationScheduler

) : ObservableUseCase<List<Song>>(schedulers) {

    override fun buildUseCaseObservable(): Observable<List<Song>> {
        return gateway.getAllNewRequest()
    }
}