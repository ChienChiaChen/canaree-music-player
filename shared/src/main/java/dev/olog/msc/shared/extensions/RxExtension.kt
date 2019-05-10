package dev.olog.msc.shared.extensions

import dev.olog.msc.shared.rx.operators.FlowableFirstThenDebounce
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

inline fun Disposable?.unsubscribe(){
    if (this != null && !isDisposed) {
        dispose()
    }
}

fun <T> Observable<T>.asFlowable(backpressureStrategy: BackpressureStrategy = BackpressureStrategy.LATEST)
        : Flowable<T> {
    return this.toFlowable(backpressureStrategy)
}



fun <T, R> Flowable<List<T>>.mapToList(mapper: (T) -> R): Flowable<List<R>> {
    return this.map { it.map { mapper(it) } }
}

fun <T, R> Observable<List<T>>.mapToList(mapper: (T) -> R): Observable<List<R>> {
    return this.map { it.map { mapper(it) } }
}

fun <T, R> Single<List<T>>.mapToList(mapper: ((T) -> R)): Single<List<R>> {
    return flatMap { Flowable.fromIterable(it).map(mapper).toList() }
}

fun <T> Observable<T>.debounceFirst(timeout: Long = 200L, unit: TimeUnit = TimeUnit.MILLISECONDS): Observable<T>{
    return this.asFlowable()
            .compose(FlowableFirstThenDebounce.get(timeout, unit))
            .toObservable()
}

fun <T> Flowable<T>.debounceFirst(timeout: Long = 200L, unit: TimeUnit = TimeUnit.MILLISECONDS): Flowable<T>{
    return this.compose(FlowableFirstThenDebounce.get(timeout, unit))
}

fun <T> Observable<T>.defer(): Observable<T> {
    return Observable.defer { this }
}

fun <T> Single<T>.defer(): Single<T> {
    return Single.defer { this }
}