package dev.olog.msc.data.repository.util

import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flowViaChannel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ContentResolverFlow @Inject constructor(
    @ApplicationContext context: Context
) {

    private val contentResolver = context.contentResolver

    private val contentObserverHandler = Handler(Looper.getMainLooper())



    suspend fun <T> createQuery(
        uri: Uri, projection: Array<String>?,
        selection: String?, selectionArgs: Array<String>?,
        sortOrder: String?, notifyForDescendents: Boolean
    ): QueryFlow<T> = coroutineScope {

        val flow = flowViaChannel<Query<T>> { channel ->

            val query = object : Query<T>() {
                override fun run(): Cursor? {
                    return contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
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
            contentResolver.registerContentObserver(uri, notifyForDescendents, observer)
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

}