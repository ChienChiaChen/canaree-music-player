package dev.olog.msc.domain.interactor.all.recently.added

import dev.olog.msc.core.entity.Album
import dev.olog.msc.core.executor.IoScheduler
import dev.olog.msc.core.prefs.AppPreferencesGateway
import dev.olog.msc.domain.interactor.all.GetAllAlbumsUseCase
import dev.olog.msc.domain.interactor.all.GetAllSongsUseCase
import dev.olog.msc.core.interactor.base.ObservableUseCase
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

class GetRecentlyAddedAlbumsUseCase @Inject constructor(
    scheduler: IoScheduler,
    private val getAllAlbumsUseCase: GetAllAlbumsUseCase,
    private val getAllSongsUseCase: GetAllSongsUseCase,
    private val appPreferencesUseCase: AppPreferencesGateway

) : ObservableUseCase<List<Album>>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(): Observable<List<Album>> {
        return Observables.combineLatest(
                getRecentlyAddedSong(getAllSongsUseCase),
                getAllAlbumsUseCase.execute(),
                appPreferencesUseCase.observeLibraryNewVisibility()
        ) { songs, albums, show ->
            if (show){
                albums.filter { album -> songs.any { song -> song.albumId == album.id } }
            } else {
                listOf()
            }

        }
    }
}