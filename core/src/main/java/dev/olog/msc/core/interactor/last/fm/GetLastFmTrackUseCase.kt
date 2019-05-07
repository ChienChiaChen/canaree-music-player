package dev.olog.msc.core.interactor.last.fm

import com.github.dmstocking.optional.java.util.Optional
import dev.olog.msc.core.entity.LastFmTrack
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.LastFmGateway
import dev.olog.msc.core.interactor.base.SingleUseCaseWithParam
import io.reactivex.Single
import javax.inject.Inject

class GetLastFmTrackUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: LastFmGateway

): SingleUseCaseWithParam<Optional<LastFmTrack?>, LastFmTrackRequest>(schedulers) {

    override fun buildUseCaseObservable(param: LastFmTrackRequest): Single<Optional<LastFmTrack?>> {
        val (id) = param
        return gateway.getTrack(id)
    }
}

data class LastFmTrackRequest(
        val id: Long,
        val title: String,
        val artist: String,
        val album: String
)