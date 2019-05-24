package dev.olog.msc.core.interactor.base

import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.getAll
import dev.olog.msc.core.executors.IoDispatcher
import dev.olog.msc.core.gateway.base.BaseGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

abstract class GetGroupUseCase<T>(
    private val gateway: BaseGateway<T, *>,
    schedulers: IoDispatcher
) : ObservableFlow<List<T>>(schedulers) {


    override suspend fun buildUseCaseObservable(): Flow<List<T>> = flowOf(gateway.getAll().getAll(Filter.NO_FILTER))
}