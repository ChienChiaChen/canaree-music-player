package dev.olog.msc.domain.interactor.all.most.played

import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.core.gateway.FolderGateway
import dev.olog.msc.core.gateway.GenreGateway
import dev.olog.msc.core.gateway.PlaylistGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import io.reactivex.Completable
import javax.inject.Inject

class InsertMostPlayedUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val folderGateway: FolderGateway,
        private val playlistGateway: PlaylistGateway,
        private val genreGateway: GenreGateway

) : CompletableUseCaseWithParam<MediaId>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Completable {
        return when (mediaId.category){
            MediaIdCategory.FOLDERS -> folderGateway.insertMostPlayed(mediaId)
            MediaIdCategory.PLAYLISTS -> playlistGateway.insertMostPlayed(mediaId)
            MediaIdCategory.GENRES -> genreGateway.insertMostPlayed(mediaId)
            else -> Completable.complete()
        }
    }

}