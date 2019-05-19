package dev.olog.msc.data.repository.util

import android.content.ContentResolver
import android.database.Cursor

@Suppress("unused")
internal inline fun <T> ContentResolver.queryAll(
    cursor: Cursor,
    mapper: (Cursor) -> T,
    noinline afterQuery: ((List<T>) -> List<T>)?
): List<T> {
    val result = mutableListOf<T>()
    while (cursor.moveToNext()) {
        result.add(mapper(cursor))
    }
    cursor.close()

    return afterQuery?.invoke(result) ?: result
}

@Suppress("unused")
internal inline fun <T> ContentResolver.querySingle(
    cursor: Cursor,
    mapper: (Cursor) -> T,
    noinline afterQuery: ((T) -> T)?
): T {
    val item: T?
    cursor.moveToFirst()
    item = mapper(cursor)
    cursor.close()

    return afterQuery?.invoke(item) ?: item
}

@Suppress("unused")
internal inline fun <T> ContentResolver.queryMaybe(
    cursor: Cursor,
    mapper: (Cursor) -> T?,
    noinline afterQuery: ((T) -> T)?
): T? {
    var item: T? = null
    if (cursor.moveToFirst()) {
        item = mapper(cursor)
    }

    cursor.close()

    if (item != null && afterQuery != null) {
        item = afterQuery.invoke(item)
    }

    return item
}

@Suppress("unused")
internal fun ContentResolver.queryFirstColumn(cursor: Cursor): Int {
    var size = 0
    cursor.moveToFirst()
    size = cursor.getInt(0)
    cursor.close()
    return size
}

@Suppress("unused")
internal fun ContentResolver.queryCountRow(cursor: Cursor): Int {
    val count = cursor.count
    cursor.close()
    return count
}