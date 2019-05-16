package dev.olog.msc.data.entity.custom

import android.content.ContentResolver
import android.net.Uri
import dev.olog.msc.core.entity.Page
import dev.olog.msc.core.entity.PageRequest
import dev.olog.msc.data.repository.util.ContentObserverFlow
import dev.olog.msc.data.repository.util.queryAll
import dev.olog.msc.data.repository.util.queryCountRow
import dev.olog.msc.shared.utils.assertBackgroundThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class PageRequestImpl<T>(
    private val cursorFactory: CursorFactory,
    private val cursorMapper: CursorMapper<T>,
    private val listMapper: ListMapper<T>?,
    private val contentResolver: ContentResolver,
    private val contentObserverFlow: ContentObserverFlow,
    private val mediaStoreUri: Uri
) : PageRequest<T> {

    override fun getPage(page: Page): List<T> {
        assertBackgroundThread()
        return contentResolver.queryAll(cursorFactory(page), cursorMapper, listMapper)
    }

    override fun getCount(): Int {
        assertBackgroundThread()
        return contentResolver.queryCountRow(cursorFactory(null))
    }

    override suspend fun observePage(page: Page): Flow<List<T>> {
        return contentObserverFlow.createQuery<T>(cursorFactory, page, mediaStoreUri, true)
            .mapToList { cursorMapper(it) }
            .map { listMapper?.invoke(it) ?: it }
            .distinctUntilChanged()
    }

    override suspend fun observeNotification(): Flow<Unit> {
        return contentObserverFlow.createNotification(mediaStoreUri)
    }
}