package dev.olog.msc.core.interactor.base

import dev.olog.msc.core.executor.Schedulers
import io.reactivex.Single


abstract class SingleUseCaseWithParam<T, Param>(
        private val schedulers: Schedulers
) {

    protected abstract fun buildUseCaseObservable(param: Param): Single<T>

    fun execute(param: Param): Single<T> {
        return Single.defer { this.buildUseCaseObservable(param)
                .subscribeOn(schedulers.worker)
                .observeOn(schedulers.ui) }
    }

}
