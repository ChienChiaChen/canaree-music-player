package dev.olog.msc.domain.interactor.all.recently.added

import dev.olog.msc.core.entity.PodcastArtist
import dev.olog.msc.core.executor.IoScheduler
import dev.olog.msc.core.prefs.AppPreferencesGateway
import dev.olog.msc.domain.interactor.all.GetAllPodcastArtistsUseCase
import dev.olog.msc.domain.interactor.all.GetAllPodcastUseCase
import dev.olog.msc.core.interactor.base.ObservableUseCase
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

class GetRecentlyAddedPodcastsArtistsUseCase @Inject constructor(
    scheduler: IoScheduler,
    private val getAllArtistsUseCase: GetAllPodcastArtistsUseCase,
    private val getAllPodcastsUseCase: GetAllPodcastUseCase,
    private val appPreferencesUseCase: AppPreferencesGateway

) : ObservableUseCase<List<PodcastArtist>>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(): Observable<List<PodcastArtist>> {
        return Observables.combineLatest(
                getRecentlyAddedPodcast(getAllPodcastsUseCase),
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