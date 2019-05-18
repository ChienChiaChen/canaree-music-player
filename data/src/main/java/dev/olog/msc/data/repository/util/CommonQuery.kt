package dev.olog.msc.data.repository.util

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.SparseArray
import dev.olog.msc.data.utils.getInt
import dev.olog.msc.data.utils.getStringOrNull

internal object CommonQuery {

    fun searchForImages(context: Context): SparseArray<String> {
        // TODO integrate in queries?
        // TODO IMPORTANT
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART), null,
            null, MediaStore.Audio.Albums._ID
        )

        val result = SparseArray<String>()

        cursor?.use {
            while (it.moveToNext()) {
                val albumArt = it.getStringOrNull(MediaStore.Audio.Albums.ALBUM_ART)
                if (albumArt != null) {
                    val id = cursor.getInt(MediaStore.Audio.Albums._ID)
                    result.append(id, albumArt)
                }
            }
        }

        return result
    }

}

@Suppress("unused")
internal inline fun <T> ContentResolver.queryAll(cursor: Cursor, mapper: (Cursor) -> T, noinline afterQuery: ((List<T>) -> List<T>)?): List<T> {
    val result = mutableListOf<T>()
    while (cursor.moveToNext()) {
        result.add(mapper(cursor))
    }
    cursor.close()

    return afterQuery?.invoke(result) ?: result
}

@Suppress("unused")
internal inline fun <T> ContentResolver.querySingle(cursor: Cursor, mapper: (Cursor) -> T, noinline afterQuery: ((T) -> T)?): T {
    val item: T?
    cursor.moveToFirst()
    item = mapper(cursor)
    cursor.close()

    return afterQuery?.invoke(item) ?: item
}

@Suppress("unused")
internal inline fun <T> ContentResolver.queryMaybe(cursor: Cursor, mapper: (Cursor) -> T?, noinline afterQuery: ((T) -> T)?): T? {
    var item: T?
    cursor.moveToFirst()
    item = mapper(cursor)
    cursor.close()

    if (item != null && afterQuery != null){
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