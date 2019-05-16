package dev.olog.msc.data.entity.custom

import android.content.ContentResolver
import android.net.Uri
import dev.olog.msc.core.entity.ItemRequest
import dev.olog.msc.data.repository.util.ContentObserverFlow
import dev.olog.msc.data.repository.util.queryMaybe
import dev.olog.msc.shared.utils.assertBackgroundThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class ItemRequestImpl<T>(
    private val cursorFactory: CursorFactory,
    private val cursorMapper: CursorMapper<T>,
    private val itemMapper: ItemMapper<T>?,
    private val contentResolver: ContentResolver,
    private val contentObserverFlow: ContentObserverFlow,
    private val mediaStoreUri: Uri
) : ItemRequest<T> {

    override fun getItem(): T? {
        assertBackgroundThread()
        return contentResolver.queryMaybe(cursorFactory(null), cursorMapper, itemMapper)
    }

    override suspend fun observeItem(): Flow<T?> {
        return contentObserverFlow.createQuery<T>(cursorFactory, null, mediaStoreUri, true)
            .mapToOne { cursorMapper(it) }
            .map {
                if (it != null) {
                    itemMapper?.invoke(it)
                } else {
                    null
                }
            }.distinctUntilChanged()
    }
}