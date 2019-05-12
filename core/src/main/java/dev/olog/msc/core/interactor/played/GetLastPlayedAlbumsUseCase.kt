//package dev.olog.msc.core.interactor.played
//
//import dev.olog.msc.core.coroutines.IoDispatcher
//import dev.olog.msc.core.coroutines.ObservableFlow
//import dev.olog.msc.core.entity.track.Album
//import dev.olog.msc.core.gateway.AlbumGateway
//import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
//import io.reactivex.BackpressureStrategy
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.reactive.flow.asFlow
//import javax.inject.Inject
//
//class GetLastPlayedAlbumsUseCase @Inject constructor(
//    schedulers: IoDispatcher,
//    private val albumGateway: AlbumGateway,
//    private val appPreferencesUseCase: AppPreferencesGateway
//
//) : ObservableFlow<List<Album>>(schedulers) {
//
//    override suspend fun buildUseCaseObservable(): Flow<List<Album>> {
//        return albumGateway.getLastPlayed()
//            .combineLatest(appPreferencesUseCase.canShowLibraryRecentPlayedVisibility().toFlowable(BackpressureStrategy.LATEST).asFlow())
//            { albums, show ->
//                if (show) {
//                    albums
//                } else {
//                    listOf()
//                }
//            }
//    }
//}