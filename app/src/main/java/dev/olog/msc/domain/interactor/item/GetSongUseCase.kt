package dev.olog.msc.domain.interactor.item

import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.SongGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCaseWithParam
import dev.olog.msc.core.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class GetSongUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: SongGateway

) : ObservableUseCaseWithParam<Song, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<Song> {
        return gateway.getByParam(mediaId.resolveId)
    }
}
