package dev.olog.msc.core.interactor.played

import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.executors.ComputationScheduler
import dev.olog.msc.core.gateway.ArtistGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.interactor.base.ObservableUseCase
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

class GetLastPlayedArtistsUseCase @Inject constructor(
        schedulers: ComputationScheduler,
        private val artistGateway: ArtistGateway,
        private val appPreferencesUseCase: AppPreferencesGateway

): ObservableUseCase<List<Artist>>(schedulers) {

    override fun buildUseCaseObservable(): Observable<List<Artist>> {
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