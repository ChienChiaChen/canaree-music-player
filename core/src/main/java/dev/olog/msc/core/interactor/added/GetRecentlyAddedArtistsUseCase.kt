//package dev.olog.msc.core.interactor.added
//
//import dev.olog.msc.core.coroutines.IoDispatcher
//import dev.olog.msc.core.coroutines.ObservableFlow
//import dev.olog.msc.core.coroutines.combineLatest
//import dev.olog.msc.core.entity.track.Artist
//import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
//import dev.olog.msc.core.interactor.all.GetAllArtistsUseCase
//import dev.olog.msc.core.interactor.all.GetAllSongsUseCase
//import io.reactivex.BackpressureStrategy
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.reactive.flow.asFlow
//import javax.inject.Inject
//
//class GetRecentlyAddedArtistsUseCase @Inject constructor(
//    scheduler: IoDispatcher,
//    private val getAllArtistsUseCase: GetAllArtistsUseCase,
//    private val getAllSongsUseCase: GetAllSongsUseCase,
//    private val appPreferencesUseCase: AppPreferencesGateway
//
//) : ObservableFlow<List<Artist>>(scheduler) {
//
//    override suspend fun buildUseCaseObservable(): Flow<List<Artist>> {
//        return combineLatest(
//            getRecentlyAddedSong(getAllSongsUseCase),
//            getAllArtistsUseCase.execute(),
//            appPreferencesUseCase.canShowLibraryNewVisibility().toFlowable(BackpressureStrategy.LATEST).asFlow()
//        ) { songs, artists, show ->
//            if (show) {
//                artists.filter { artist -> songs.any { song -> song.artistId == artist.id } }
//            } else {
//                listOf()
//            }
//        }
//    }
//}