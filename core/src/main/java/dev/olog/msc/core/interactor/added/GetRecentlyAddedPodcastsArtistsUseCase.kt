package dev.olog.msc.core.interactor.added

import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.coroutines.ObservableFlow
import dev.olog.msc.core.coroutines.combineLatest
import dev.olog.msc.core.entity.podcast.PodcastArtist
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.interactor.all.GetAllPodcastArtistsUseCase
import dev.olog.msc.core.interactor.all.GetAllPodcastUseCase
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

class GetRecentlyAddedPodcastsArtistsUseCase @Inject constructor(
    scheduler: IoDispatcher,
    private val getAllArtistsUseCase: GetAllPodcastArtistsUseCase,
    private val getAllPodcastsUseCase: GetAllPodcastUseCase,
    private val appPreferencesUseCase: AppPreferencesGateway

) : ObservableFlow<List<PodcastArtist>>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun buildUseCaseObservable(): Flow<List<PodcastArtist>> {
        return combineLatest(
            getRecentlyAddedPodcast(getAllPodcastsUseCase),
            getAllArtistsUseCase.execute(),
            Flowable.just(appPreferencesUseCase.canShowLibraryNewVisibility()).asFlow()
        ) { songs, artists, show ->
            if (show) {
                artists.filter { artist -> songs.any { song -> song.artistId == artist.id } }
            } else {
                listOf()
            }
        }
    }
}