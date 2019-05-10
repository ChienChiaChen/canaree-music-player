package dev.olog.msc.core.interactor.base

import dev.olog.msc.core.executors.Schedulers
import io.reactivex.Single


abstract class SingleUseCase<T>(
    private val schedulers: Schedulers
) {

    protected abstract fun buildUseCaseObservable(): Single<T>

    fun execute(): Single<T> {
        return this.buildUseCaseObservable()
            .subscribeOn(schedulers.worker)
            .observeOn(schedulers.ui)
    }

}
