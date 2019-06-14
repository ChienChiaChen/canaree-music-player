package dev.olog.msc.domain.interactor.all

import dev.olog.msc.core.entity.Playlist
import dev.olog.msc.core.executor.ComputationScheduler
import dev.olog.msc.core.gateway.PlaylistGateway
import dev.olog.msc.core.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class GetAllAutoPlaylistUseCase @Inject constructor(
    schedulers: ComputationScheduler,
    private val gateway: PlaylistGateway

) : ObservableUseCase<List<Playlist>>(schedulers) {

    override fun buildUseCaseObservable(): Observable<List<Playlist>> {
        return gateway.getAllAutoPlaylists()
    }
}