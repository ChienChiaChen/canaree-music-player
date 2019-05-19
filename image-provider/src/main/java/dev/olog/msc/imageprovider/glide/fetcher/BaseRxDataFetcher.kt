package dev.olog.msc.imageprovider.glide.fetcher

import android.content.Context
import android.preference.PreferenceManager
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.data.HttpUrlFetcher
import com.bumptech.glide.load.model.GlideUrl
import dev.olog.msc.core.PrefsKeys
import dev.olog.msc.shared.utils.NetworkUtils
import kotlinx.coroutines.*
import java.io.InputStream
import java.util.concurrent.atomic.AtomicLong

/**
 * Class that uses a 'global' counter to delay the image request for an image.
 * Because LastFm allows 5 request per second for every IP
 */
abstract class BaseRxDataFetcher(
    private val context: Context,
    private val prefsKeys: PrefsKeys

) : DataFetcher<InputStream> {

    companion object {
        private const val TIMEOUT = 2500

        private var counter = AtomicLong(1)
    }

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    private var job: Job? = null

    private var hasIncremented = false
    private var hasAlreadyDecremented = false

    override fun getDataClass(): Class<InputStream> = InputStream::class.java

    override fun getDataSource(): DataSource = DataSource.REMOTE

    override fun cleanup() {
        unsubscribe()
    }

    override fun cancel() {
        unsubscribe()
    }

    private fun unsubscribe() {
        job?.cancel()
        if (hasIncremented && !hasAlreadyDecremented) {
            counter.decrementAndGet()
        }
    }

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        job = GlobalScope.launch {
            try {
                if (shouldFetch()) {
                    delayRequest()
                    yield()
                }
                val image = execute(priority, callback)
                yield()

                if (image.isNotBlank() && networkSafeAction()){
                    val urlFetcher = HttpUrlFetcher(
                        GlideUrl(image),
                        TIMEOUT
                    )
                    urlFetcher.loadData(priority, callback)
                    return@launch
                }
                callback.onLoadFailed(NoSuchElementException())
            } catch (ex: NullPointerException){
                callback.onLoadFailed(ex)
            }
        }
    }

    private suspend fun delayRequest() {
        val current = counter.incrementAndGet()
        hasIncremented = true
        delay(current * threshold)
        hasAlreadyDecremented = true
        counter.decrementAndGet()
    }

    private fun networkSafeAction(): Boolean {

        val downloadMode = prefs.getString(
            context.getString(prefsKeys.autoDownloadImages()),
            context.getString(prefsKeys.defaultAutoDownloadImages())
        )

        val isWifiActive = NetworkUtils.isOnWiFi(context)

        when (downloadMode) {
            context.getString(prefsKeys.autoDownloadImageNever()) -> {
            }
            context.getString(prefsKeys.autoDownloadImageWifiOnly()) -> {
                if (isWifiActive) {
                    return true
                }
            }
            context.getString(prefsKeys.autoDownloadImageAlways()) -> return true
        }
        return false
    }

    protected abstract suspend fun execute(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>): String

    protected abstract suspend fun shouldFetch(): Boolean

    protected abstract val threshold: Long

}