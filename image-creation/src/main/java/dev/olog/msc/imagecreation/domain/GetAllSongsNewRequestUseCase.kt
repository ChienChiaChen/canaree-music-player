package dev.olog.msc.imagecreation.domain

import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.executors.ComputationScheduler
import dev.olog.msc.core.gateway.SongGateway
import dev.olog.msc.core.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

internal class GetAllSongsNewRequestUseCase @Inject constructor(
        private val gateway: SongGateway,
        schedulers: ComputationScheduler

) : ObservableUseCase<List<Song>>(schedulers) {

    override fun buildUseCaseObservable(): Observable<List<Song>> {
        return gateway.getAllNewRequest()
    }
}