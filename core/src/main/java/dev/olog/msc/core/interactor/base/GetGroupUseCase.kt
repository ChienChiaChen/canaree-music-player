package dev.olog.msc.core.interactor.base

import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.coroutines.ObservableFlow
import dev.olog.msc.core.gateway.BaseGateway
import kotlinx.coroutines.flow.Flow

abstract class GetGroupUseCase<T>(
    private val gateway: BaseGateway<T, *>,
    schedulers: IoDispatcher
) : ObservableFlow<List<T>>(schedulers) {


    override suspend fun buildUseCaseObservable(): Flow<List<T>> = gateway.getAll()
}