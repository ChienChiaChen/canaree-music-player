package dev.olog.msc.data.repository.util

import android.database.Cursor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class QueryFlow<T>(
    private var flow: Flow<Query<T>>
) {

    fun extendFlow(mapper: (Flow<Query<T>>) -> Flow<Query<T>>): QueryFlow<T> {
        flow = mapper(flow)
        return this
    }

    fun mapToList(mapper: (Cursor) -> T): Flow<List<T>> {
        return flow.map {
            it.run().use { cursor ->
                val result = ArrayList<T>(cursor?.count ?: 0)
                while (cursor?.moveToNext() == true) {
                    result.add(mapper(cursor))
                }
                result
            }
        }
    }

    fun mapToOne(mapper: (Cursor) -> T?): Flow<T?> {
        return flow.map {
            it.run().use { cursor ->
                if (cursor?.moveToFirst() == true) {
                    mapper(cursor)
                } else {
                    null
                }

            }
        }
    }

}