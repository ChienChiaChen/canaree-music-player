package dev.olog.msc.core.interactor.base

import dev.olog.msc.core.executors.Schedulers
import io.reactivex.Single


abstract class SingleUseCaseWithParam<T, Param>(
    private val schedulers: Schedulers
) {

    protected abstract fun buildUseCaseObservable(param: Param): Single<T>

    fun execute(param: Param): Single<T> {
        return this.buildUseCaseObservable(param)
            .subscribeOn(schedulers.worker)
            .observeOn(schedulers.ui)
    }

}
