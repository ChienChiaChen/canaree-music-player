package dev.olog.msc.domain.interactor.played.most

import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.core.gateway.FolderGateway
import dev.olog.msc.core.gateway.GenreGateway
import dev.olog.msc.core.gateway.PlaylistGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCaseWithParam
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import io.reactivex.Observable
import javax.inject.Inject

class GetMostPlayedSongsUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val folderGateway: FolderGateway,
        private val playlistGateway: PlaylistGateway,
        private val genreGateway: GenreGateway

) : ObservableUseCaseWithParam<List<Song>, MediaId>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<Song>> {

        return when (mediaId.category) {
            MediaIdCategory.GENRES -> return genreGateway.getMostPlayed(mediaId)
                    .distinctUntilChanged()
            MediaIdCategory.PLAYLISTS -> return playlistGateway.getMostPlayed(mediaId)
                    .distinctUntilChanged()
            MediaIdCategory.FOLDERS -> folderGateway.getMostPlayed(mediaId)
                    .distinctUntilChanged()
            else -> Observable.just(listOf())
        }
    }
}