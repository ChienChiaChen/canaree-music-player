package dev.olog.msc.core.utils

@Suppress("NOTHING_TO_INLINE")
inline fun Int.negative(): Int {
    if (this > 0) {
        return -this
    }
    return this
}

@Suppress("NOTHING_TO_INLINE")
inline fun Long.negative(): Long {
    if (this > 0) {
        return -this
    }
    return this
}