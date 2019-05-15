package dev.olog.msc.data.repository.util

import android.database.Cursor

internal abstract class Query<T> {
    abstract fun run(): Cursor?
}