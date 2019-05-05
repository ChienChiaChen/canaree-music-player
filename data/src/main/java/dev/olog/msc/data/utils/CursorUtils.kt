@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.msc.data.utils

import android.database.Cursor

internal inline fun Cursor.getInt(columnName: String): Int {
    return this.getInt(this.getColumnIndex(columnName))
}

internal inline fun Cursor.getLong(columnName: String): Long {
    return this.getLong(this.getColumnIndex(columnName))
}

internal inline fun Cursor.getString(columnName: String): String {
    return this.getString(this.getColumnIndex(columnName))
}

internal inline fun Cursor.getIntOrNull(columnName: String): Int? {
    return this.getInt(this.getColumnIndex(columnName))
}

internal inline fun Cursor.getLongOrNull(columnName: String): Long? {
    return this.getLong(this.getColumnIndex(columnName))
}

internal inline fun Cursor.getStringOrNull(columnName: String): String? {
    return this.getString(this.getColumnIndex(columnName))
}

internal inline fun Cursor.getStringOrNull(columnIndex: Int): String? {
    return getString(columnIndex)
}