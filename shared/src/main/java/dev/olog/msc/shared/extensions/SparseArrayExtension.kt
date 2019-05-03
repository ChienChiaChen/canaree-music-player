package dev.olog.msc.shared.extensions

import android.util.LongSparseArray

inline fun <T> LongSparseArray<T>.forEach(action: (key: Long, value: T) -> Unit) {
    for (index in 0 until size()) {
        action(keyAt(index), valueAt(index))
    }
}

fun <T> LongSparseArray<T>.toList(): List<T>{
    val list = mutableListOf<T>()

    this.forEach { _, value -> list.add(value) }

    return list
}

fun <T> LongSparseArray<T>.toggle(key: Long, item: T){
    val current = this.get(key)
    if (current == null){
        this.append(key, item)
    } else {
        this.remove(key)
    }
}