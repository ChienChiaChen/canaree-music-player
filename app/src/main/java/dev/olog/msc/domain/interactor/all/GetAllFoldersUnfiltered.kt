package dev.olog.msc.domain.interactor.all

import dev.olog.msc.core.entity.Folder
import dev.olog.msc.core.executor.IoScheduler
import dev.olog.msc.core.gateway.FolderGateway
import dev.olog.msc.core.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class GetAllFoldersUnfiltered @Inject constructor(
    scheduler: IoScheduler,
    private val gateway: FolderGateway

) : ObservableUseCase<List<Folder>>(scheduler) {

    override fun buildUseCaseObservable(): Observable<List<Folder>> {
        return gateway.getAllUnfiltered()
    }
}