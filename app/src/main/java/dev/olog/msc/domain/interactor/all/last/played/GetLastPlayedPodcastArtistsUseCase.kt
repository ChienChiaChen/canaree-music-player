package dev.olog.msc.domain.interactor.all.last.played

import dev.olog.msc.core.entity.PodcastArtist
import dev.olog.msc.core.executor.ComputationScheduler
import dev.olog.msc.core.gateway.PodcastArtistGateway
import dev.olog.msc.core.prefs.AppPreferencesGateway
import dev.olog.msc.core.interactor.base.ObservableUseCase
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

class GetLastPlayedPodcastArtistsUseCase @Inject constructor(
    schedulers: ComputationScheduler,
    private val artistGateway: PodcastArtistGateway,
    private val appPreferencesUseCase: AppPreferencesGateway

): ObservableUseCase<List<PodcastArtist>>(schedulers) {

    override fun buildUseCaseObservable(): Observable<List<PodcastArtist>> {
        return Observables.combineLatest(
                artistGateway.getLastPlayed(),
                appPreferencesUseCase.observeLibraryRecentPlayedVisibility()) { artists, show ->
            if (show){
                artists
            } else {
                listOf()
            }
        }
    }
}