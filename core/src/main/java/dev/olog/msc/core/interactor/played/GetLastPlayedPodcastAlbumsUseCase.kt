package dev.olog.msc.core.interactor.played

import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.coroutines.ObservableFlow
import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.gateway.PodcastAlbumGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import io.reactivex.Flowable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combineLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

class GetLastPlayedPodcastAlbumsUseCase @Inject constructor(
    schedulers: IoDispatcher,
    private val albumGateway: PodcastAlbumGateway,
    private val appPreferencesUseCase: AppPreferencesGateway

) : ObservableFlow<List<PodcastAlbum>>(schedulers) {

    override suspend fun buildUseCaseObservable(): Flow<List<PodcastAlbum>> {
        return flowOf(listOf())
//        return Flowable.just(albumGateway.getLastPlayed()).asFlow()
//            .combineLatest(Flowable.just(appPreferencesUseCase.canShowLibraryRecentPlayedVisibility()).asFlow())
//            { albums, show ->
//                if (show) {
//                    albums
//                } else {
//                    listOf()
//                }
//            }
    }
}