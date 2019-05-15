package dev.olog.msc.core.interactor.added

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.interactor.base.ObservableUseCaseWithParam
import io.reactivex.Observable
import javax.inject.Inject

class GetRecentlyAddedUseCase @Inject constructor(
    scheduler: IoScheduler

) : ObservableUseCaseWithParam<List<Song>, MediaId>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<Song>> {
        return TODO()
//        val time = System.currentTimeMillis()
//        if (mediaId.isFolder || mediaId.isGenre) {
//            return getSongListByParamUseCase.execute(mediaId)
//                .map { if (it.size >= 5) it else listOf() }
//                .map { songList -> songList.filter { (time - it.dateAdded * 1000) <= TWO_WEEKS } }
//        }
//        return Observable.just(listOf())
    }
}