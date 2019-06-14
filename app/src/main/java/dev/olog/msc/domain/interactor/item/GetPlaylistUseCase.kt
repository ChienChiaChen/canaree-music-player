package dev.olog.msc.domain.interactor.item

import dev.olog.msc.core.entity.Playlist
import dev.olog.msc.core.executor.IoScheduler
import dev.olog.msc.core.gateway.PlaylistGateway
import dev.olog.msc.core.interactor.base.ObservableUseCaseWithParam
import dev.olog.msc.core.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class GetPlaylistUseCase @Inject internal constructor(
    schedulers: IoScheduler,
    private val gateway: PlaylistGateway

) : ObservableUseCaseWithParam<Playlist, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<Playlist> {
        return gateway.getByParam(mediaId.categoryId)
    }
}
