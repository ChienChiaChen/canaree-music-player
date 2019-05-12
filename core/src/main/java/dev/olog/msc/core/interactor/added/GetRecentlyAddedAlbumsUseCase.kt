//package dev.olog.msc.core.interactor.added
//
//import dev.olog.msc.core.coroutines.IoDispatcher
//import dev.olog.msc.core.coroutines.ObservableFlow
//import dev.olog.msc.core.coroutines.combineLatest
//import dev.olog.msc.core.entity.track.Album
//import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
//import dev.olog.msc.core.interactor.all.GetAllAlbumsUseCase
//import dev.olog.msc.core.interactor.all.GetAllSongsUseCase
//import io.reactivex.BackpressureStrategy
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.reactive.flow.asFlow
//import javax.inject.Inject
//
//class GetRecentlyAddedAlbumsUseCase @Inject constructor(
//    scheduler: IoDispatcher,
//    private val getAllAlbumsUseCase: GetAllAlbumsUseCase,
//    private val getAllSongsUseCase: GetAllSongsUseCase,
//    private val appPreferencesUseCase: AppPreferencesGateway
//
//) : ObservableFlow<List<Album>>(scheduler) {
//
//    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
//    override suspend fun buildUseCaseObservable(): Flow<List<Album>> {
//        return combineLatest(
//            getRecentlyAddedSong(getAllSongsUseCase),
//            getAllAlbumsUseCase.execute(),
//            appPreferencesUseCase.canShowLibraryNewVisibility().toFlowable(BackpressureStrategy.LATEST).asFlow()
//        ) { songs, albums, show ->
//            if (show) {
//                albums.filter { album -> songs.any { song -> song.albumId == album.id } }
//            } else {
//                listOf()
//            }
//        }
//    }
//}