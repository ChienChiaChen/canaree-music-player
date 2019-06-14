package dev.olog.msc.domain.interactor.item

import dev.olog.msc.core.entity.Song
import dev.olog.msc.core.executor.IoScheduler
import dev.olog.msc.core.gateway.SongGateway
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
