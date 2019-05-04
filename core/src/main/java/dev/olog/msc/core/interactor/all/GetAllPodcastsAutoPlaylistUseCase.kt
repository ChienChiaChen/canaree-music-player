package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.entity.podcast.PodcastPlaylist
import dev.olog.msc.core.executors.ComputationScheduler
import dev.olog.msc.core.gateway.PodcastPlaylistGateway
import dev.olog.msc.core.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class GetAllPodcastsAutoPlaylistUseCase @Inject constructor(
        schedulers: ComputationScheduler,
        private val gateway: PodcastPlaylistGateway

) : ObservableUseCase<List<PodcastPlaylist>>(schedulers) {

    override fun buildUseCaseObservable(): Observable<List<PodcastPlaylist>> {
        return gateway.getAllAutoPlaylists()
    }
}