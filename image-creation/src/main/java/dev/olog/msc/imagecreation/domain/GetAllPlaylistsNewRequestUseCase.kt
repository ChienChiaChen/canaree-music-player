package dev.olog.msc.imagecreation.domain

import dev.olog.msc.core.entity.track.Playlist
import dev.olog.msc.core.executors.ComputationScheduler
import dev.olog.msc.core.gateway.PlaylistGateway
import dev.olog.msc.core.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

internal class GetAllPlaylistsNewRequestUseCase @Inject constructor(
        schedulers: ComputationScheduler,
        private val gateway: PlaylistGateway

) : ObservableUseCase<List<Playlist>>(schedulers) {

    override fun buildUseCaseObservable(): Observable<List<Playlist>> {
        return gateway.getAllNewRequest()
    }
}