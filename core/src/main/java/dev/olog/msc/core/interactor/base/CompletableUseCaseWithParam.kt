package dev.olog.msc.core.interactor.base

import dev.olog.msc.core.executors.Schedulers
import io.reactivex.Completable

abstract class CompletableUseCaseWithParam<in Param>(
        private val schedulers: Schedulers
) {

    protected abstract fun buildUseCaseObservable(param: Param): Completable

    fun execute(param: Param): Completable {
        return this.buildUseCaseObservable(param)
                .subscribeOn(schedulers.worker)
                .observeOn(schedulers.ui)
    }

}