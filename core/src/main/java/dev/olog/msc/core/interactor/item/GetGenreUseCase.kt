package dev.olog.msc.core.interactor.item

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.coroutines.ObservableFlowWithParam
import dev.olog.msc.core.entity.track.Genre
import dev.olog.msc.core.gateway.track.GenreGateway
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGenreUseCase @Inject internal constructor(
    schedulers: IoDispatcher,
    private val gateway: GenreGateway

) : ObservableFlowWithParam<Genre, MediaId>(schedulers) {

    override suspend fun buildUseCaseObservable(mediaId: MediaId): Flow<Genre> {
        return gateway.observeByParam(mediaId.categoryId)
    }
}
