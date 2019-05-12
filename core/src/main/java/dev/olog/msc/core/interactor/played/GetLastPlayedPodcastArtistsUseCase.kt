package dev.olog.msc.core.interactor.played

import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.coroutines.ObservableFlow
import dev.olog.msc.core.entity.podcast.PodcastArtist
import dev.olog.msc.core.gateway.PodcastArtistGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combineLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

class GetLastPlayedPodcastArtistsUseCase @Inject constructor(
    schedulers: IoDispatcher,
    private val artistGateway: PodcastArtistGateway,
    private val appPreferencesUseCase: AppPreferencesGateway

) : ObservableFlow<List<PodcastArtist>>(schedulers) {

    override suspend fun buildUseCaseObservable(): Flow<List<PodcastArtist>> {
        return flowOf(listOf())
//        return Flowable.just(artistGateway.getLastPlayed()).asFlow()
//            .combineLatest(Flowable.just(appPreferencesUseCase.canShowLibraryRecentPlayedVisibility()).asFlow())
//            { artists, show ->
//                if (show) {
//                    artists
//                } else {
//                    listOf()
//                }
//            }
    }
}