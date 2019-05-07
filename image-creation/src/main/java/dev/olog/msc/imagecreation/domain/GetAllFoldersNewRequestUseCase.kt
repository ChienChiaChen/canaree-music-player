package dev.olog.msc.imagecreation.domain

import dev.olog.msc.core.entity.track.Folder
import dev.olog.msc.core.executors.ComputationScheduler
import dev.olog.msc.core.gateway.FolderGateway
import dev.olog.msc.core.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

internal class GetAllFoldersNewRequestUseCase @Inject constructor(
        schedulers: ComputationScheduler,
        private val gateway: FolderGateway

) : ObservableUseCase<List<Folder>>(schedulers) {

    override fun buildUseCaseObservable(): Observable<List<Folder>> {
        return gateway.getAllNewRequest()
    }
}