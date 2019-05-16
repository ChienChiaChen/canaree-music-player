package dev.olog.msc.core.interactor

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.executors.ComputationScheduler
import dev.olog.msc.core.interactor.base.ObservableUseCaseWithParam
import io.reactivex.Observable
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


class GetSongListByParamUseCase @Inject constructor(
    schedulers: ComputationScheduler

) : ObservableUseCaseWithParam<List<Song>, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<Song>> = runBlocking {
        Observable.just(listOf<Song>())  // TODO migrate to something else, like paging
    }


}
