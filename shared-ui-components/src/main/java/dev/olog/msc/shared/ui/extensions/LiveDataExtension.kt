@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.msc.shared.ui.extensions

import androidx.lifecycle.*

inline fun <T> LiveData<T>.subscribe(lifecycleOwner: LifecycleOwner, crossinline func: (T) -> Unit) {
    this.observe(lifecycleOwner, Observer {
        if (it != null) {
            func(it)
        }
    })
}

inline fun <T> liveDataOf(): MutableLiveData<T> = MutableLiveData()

fun <T, R> LiveData<T>.map(function: (T) -> R): LiveData<R> {
    return Transformations.map(this) {
        function(it)
    }
}

fun <T> LiveData<T>.filter(filter: (T) -> Boolean): LiveData<T> {
    val result = MediatorLiveData<T>()
    result.addSource<T>(this) { x ->
        if (filter(x)) {
            result.value = x
        }

    }
    return result
}

fun <T> LiveData<T>.distinctUntilChanged(): LiveData<T> {
    return Transformations.distinctUntilChanged(this)
}