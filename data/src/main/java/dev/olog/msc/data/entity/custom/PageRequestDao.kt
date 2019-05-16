package dev.olog.msc.data.entity.custom

import android.content.ContentResolver
import android.database.Cursor
import dev.olog.msc.core.entity.Page
import dev.olog.msc.core.entity.PageRequest
import dev.olog.msc.data.repository.util.queryAll
import dev.olog.msc.data.repository.util.queryCountRow
import dev.olog.msc.shared.utils.assertBackgroundThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map

internal class PageRequestDao<T>(
    private val cursorFactory: (Page) -> Cursor,
    private val cursorMapper: CursorMapper<T>,
    private val listMapper: (List<T>, Page) -> List<T>,
    private val contentResolver: ContentResolver,
    private val changeNotification: suspend () -> Flow<List<*>>,
    private val overrideSize: Int = Int.MIN_VALUE
) : PageRequest<T> {

    override fun getPage(page: Page): List<T> {
        assertBackgroundThread()
        val list = contentResolver.queryAll(cursorFactory(page), cursorMapper, null)
        return listMapper(list, page)
    }

    override fun getCount(): Int {
        assertBackgroundThread()
        if (overrideSize == Int.MIN_VALUE){
            return contentResolver.queryCountRow(cursorFactory(Page(0, Int.MAX_VALUE)))
        } else {
            return overrideSize
        }
    }

    override suspend fun observePage(page: Page): Flow<List<T>> {
//        contentObserverFlow.createQuery<T>(cursorFactory, page, mediaStoreUri, true)
//            .mapToList { cursorMapper(it) }
//            .map {
//                if (listMapper != null){
//                    listMapper(it)
//                } else {
//                    it
//                }
//            }
        return TODO("")
    }

    override suspend fun observeNotification(): Flow<Unit> {
        return changeNotification()
            .drop(1)
            .distinctUntilChanged()
            .map { Unit }
    }
}