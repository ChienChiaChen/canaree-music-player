package dev.olog.msc.core.interactor.added

import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.interactor.all.GetAllArtistsUseCase
import dev.olog.msc.core.interactor.all.GetAllSongsUseCase
import dev.olog.msc.core.interactor.base.ObservableUseCase
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

class GetRecentlyAddedArtistsUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val getAllArtistsUseCase: GetAllArtistsUseCase,
        private val getAllSongsUseCase: GetAllSongsUseCase,
        private val appPreferencesUseCase: AppPreferencesGateway

) : ObservableUseCase<List<Artist>>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(): Observable<List<Artist>> {
        return Observables.combineLatest(
                getRecentlyAddedSong(getAllSongsUseCase),
                getAllArtistsUseCase.execute(),
                appPreferencesUseCase.observeLibraryNewVisibility()
        ) { songs, artists, show ->
            if (show){
                artists.filter { artist -> songs.any { song -> song.artistId == artist.id } }
            } else {
                listOf()
            }
        }
    }
}