package dev.olog.msc.data.entity.custom

import android.database.Cursor
import dev.olog.msc.core.entity.data.request.Request

internal typealias CursorFactory = (Request?) -> Cursor
internal typealias CursorMapper<T> = (Cursor) -> T
internal typealias ListMapper<T> = (List<T>) -> List<T>
internal typealias ItemMapper<T> = (T) -> T