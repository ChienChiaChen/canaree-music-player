package dev.olog.msc.core.interactor.last.fm

import com.github.dmstocking.optional.java.util.Optional
import dev.olog.msc.core.entity.LastFmAlbum
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.LastFmGateway
import dev.olog.msc.core.interactor.base.SingleUseCaseWithParam
import io.reactivex.Single
import javax.inject.Inject

class GetLastFmAlbumUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: LastFmGateway

): SingleUseCaseWithParam<Optional<LastFmAlbum?>, LastFmAlbumRequest>(schedulers) {

    override fun buildUseCaseObservable(param: LastFmAlbumRequest): Single<Optional<LastFmAlbum?>> {
        val (id) = param
        return gateway.getAlbum(id)
    }
}

data class LastFmAlbumRequest(
        val id: Long,
        val title: String,
        val artist: String
)