package dev.olog.msc.data.repository.util

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowViaChannel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ContentObserver @Inject constructor(
    @ApplicationContext context: Context
) {

    private val contentResolver = context.contentResolver

    private val contentObserverHandler = Handler(Looper.getMainLooper())


    suspend fun createQuery(uri: Uri): Flow<Unit> = coroutineScope {

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