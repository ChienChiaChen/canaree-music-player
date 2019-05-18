package dev.olog.msc.core.interactor.all

import dev.olog.msc.core.entity.track.Folder
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.track.FolderGateway
import dev.olog.msc.core.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class ObserveAllFoldersUnfiltered @Inject constructor(
        scheduler: IoScheduler,
        private val gateway: FolderGateway

) : ObservableUseCase<List<Folder>>(scheduler) {

    override fun buildUseCaseObservable(): Observable<List<Folder>> {
        return gateway.getAllUnfiltered()
    }
}