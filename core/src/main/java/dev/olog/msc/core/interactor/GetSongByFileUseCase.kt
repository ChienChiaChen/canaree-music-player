package dev.olog.msc.core.interactor

import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.track.SongGateway
import dev.olog.msc.core.interactor.base.SingleUseCaseWithParam
import io.reactivex.Single
import javax.inject.Inject

class GetSongByFileUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: SongGateway

) : SingleUseCaseWithParam<Song, String>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(uri: String): Single<Song> {
        return gateway.getByUri(uri)
    }
}
