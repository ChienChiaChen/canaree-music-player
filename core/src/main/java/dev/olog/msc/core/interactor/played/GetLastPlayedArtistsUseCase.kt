//package dev.olog.msc.core.interactor.played
//
//import dev.olog.msc.core.coroutines.IoDispatcher
//import dev.olog.msc.core.coroutines.ObservableFlow
//import dev.olog.msc.core.entity.track.Artist
//import dev.olog.msc.core.gateway.ArtistGateway
//import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
//import io.reactivex.BackpressureStrategy
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.reactive.flow.asFlow
//import javax.inject.Inject
//
//class GetLastPlayedArtistsUseCase @Inject constructor(
//    schedulers: IoDispatcher,
//    private val artistGateway: ArtistGateway,
//    private val appPreferencesUseCase: AppPreferencesGateway
//
//) : ObservableFlow<List<Artist>>(schedulers) {
//
//    override suspend fun buildUseCaseObservable(): Flow<List<Artist>> {
//        return artistGateway.getLastPlayed()
//            .combineLatest(appPreferencesUseCase.canShowLibraryRecentPlayedVisibility().toFlowable(BackpressureStrategy.LATEST).asFlow())
//            { artists, show ->
//                if (show) {
//                    artists
//                } else {
//                    listOf()
//                }
//            }
//    }
//}