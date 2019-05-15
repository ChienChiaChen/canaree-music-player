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
            val cursor = it.run()
            if (cursor != null){
                val result = ArrayList<T>(cursor.count)
                while (cursor.moveToNext()){
                    result.add(mapper(cursor))
                }
                cursor.close()
                result
            } else {
                throw IllegalAccessError("cursor can not be null")
            }
        }
    }

    fun mapToOne(mapper: (Cursor) -> T): Flow<T> {
        return flow.map {
            val cursor = it.run()
            if (cursor != null){
                if (cursor.moveToNext()){
                    val item = mapper(cursor)
                    cursor.close()
                    item
                } else{
                    cursor.close()
                    throw IllegalAccessError("cursor can not found")
                }
            } else {
                throw IllegalAccessError("cursor can not be null")
            }
        }
    }

}