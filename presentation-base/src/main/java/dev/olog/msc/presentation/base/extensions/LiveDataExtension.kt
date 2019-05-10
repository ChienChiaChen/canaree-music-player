@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.msc.presentation.base.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable

inline fun <T> LiveData<T>.subscribe(lifecycleOwner: LifecycleOwner, crossinline func: (T) -> Unit) {
    this.observe(lifecycleOwner, androidx.lifecycle.Observer {
        if (it != null){
            func(it)
        }
    })
}

inline fun <T> Flowable<T>.asLiveData() : LiveData<T> {
    return LiveDataReactiveStreams.fromPublisher(this)
}

inline fun <T> Observable<T>.asLiveData(backpressureStrategy: BackpressureStrategy = BackpressureStrategy.LATEST)
        : LiveData<T> {

    return LiveDataReactiveStreams.fromPublisher(this.toFlowable(backpressureStrategy))

}