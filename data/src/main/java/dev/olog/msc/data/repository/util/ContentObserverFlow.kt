package dev.olog.msc.data.repository.util

import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.data.request.Request
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowViaChannel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ContentObserverFlow @Inject constructor(
    @ApplicationContext context: Context
) {

    private val contentResolver = context.contentResolver

    private val contentObserverHandler = Handler(Looper.getMainLooper())


    suspend fun <T> createQuery(
        cursor: (Request?) -> Cursor,
        page: Request?,
        notificationUri: Uri,
        notifyForDescendents: Boolean
    ): QueryFlow<T> = coroutineScope {

        val flow = flowViaChannel<Query<T>> { channel ->

            val query = object : Query<T>() {
                override fun run(): Cursor? {
                    return cursor(page)
                }
            }

            val observer = object : ContentObserver(contentObserverHandler) {
                override fun onChange(selfChange: Boolean) {
                    if (!channel.isClosedForSend) {
                        GlobalScope.launch {
                            channel.send(query)
                        }
                    }

                }
            }
            contentResolver.registerContentObserver(notificationUri, notifyForDescendents, observer)
            GlobalScope.launch {
                if (!channel.isClosedForSend) {
                    channel.send(query)
                }
            }

            channel.invokeOnClose {
                contentResolver.unregisterContentObserver(observer)
            }
        }


        QueryFlow(flow)
    }

    suspend fun createNotification(uri: Uri): Flow<Unit> = coroutineScope {

        val flow = flowViaChannel<Unit> { channel ->

            val observer = object : ContentObserver(contentObserverHandler) {
                override fun onChange(selfChange: Boolean) {
                    if (!channel.isClosedForSend) {
                        GlobalScope.launch {
                            channel.send(Unit)
                        }
                    }

                }
            }
            contentResolver.registerContentObserver(uri, true, observer)

            channel.invokeOnClose {
                contentResolver.unregisterContentObserver(observer)
            }
        }

        flow
    }

}