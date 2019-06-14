package dev.olog.msc.domain.interactor.playing.queue

import dev.olog.msc.core.executor.IoScheduler
import dev.olog.msc.core.gateway.PlayingQueueGateway
import dev.olog.msc.core.interactor.base.CompletableUseCaseWithParam
import dev.olog.msc.music.service.model.MediaEntity
import io.reactivex.Completable
import javax.inject.Inject

class UpdateMiniQueueUseCase @Inject constructor(
    schedulers: IoScheduler,
    private val gateway: PlayingQueueGateway
) : CompletableUseCaseWithParam<List<MediaEntity>>(schedulers) {

    override fun buildUseCaseObservable(param: List<MediaEntity>): Completable {
        return Completable.fromCallable {
            gateway.updateMiniQueue(param.map { it.idInPlaylist to it.id })
        }
    }
}