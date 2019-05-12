package dev.olog.msc.core.interactor.added

import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.coroutines.ObservableFlow
import dev.olog.msc.core.coroutines.combineLatest
import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.interactor.all.GetAllPodcastAlbumsUseCase
import dev.olog.msc.core.interactor.all.GetAllPodcastUseCase
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

class GetRecentlyAddedPodcastsAlbumsUseCase @Inject constructor(
    scheduler: IoDispatcher,
    private val getAllAlbumsUseCase: GetAllPodcastAlbumsUseCase,
    private val getAllPodcastsUseCase: GetAllPodcastUseCase,
    private val appPreferencesUseCase: AppPreferencesGateway

) : ObservableFlow<List<PodcastAlbum>>(scheduler) {

    override suspend fun buildUseCaseObservable(): Flow<List<PodcastAlbum>> {
        return combineLatest(
            getRecentlyAddedPodcast(getAllPodcastsUseCase),
            getAllAlbumsUseCase.execute(),
            Flowable.just(appPreferencesUseCase.canShowLibraryNewVisibility()).asFlow()
        ) { songs, albums, show ->
            if (show) {
                albums.filter { album -> songs.any { song -> song.albumId == album.id } }
            } else {
                listOf()
            }

        }
    }
}