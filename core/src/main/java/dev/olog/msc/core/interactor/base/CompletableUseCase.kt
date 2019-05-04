package dev.olog.msc.core.interactor.base

import dev.olog.msc.core.executors.Schedulers
import io.reactivex.Completable

abstract class CompletableUseCase(
        private val schedulers: Schedulers
) {

    protected abstract fun buildUseCaseObservable(): Completable

    fun execute(): Completable {
        return Completable.defer { this.buildUseCaseObservable()
                .subscribeOn(schedulers.worker)
                .observeOn(schedulers.ui) }
    }

}