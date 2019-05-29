package dev.olog.msc.imageprovider.glide.fetcher

import android.content.Context
import com.bumptech.glide.Priority
import com.bumptech.glide.load.data.DataFetcher
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.gateway.LastFmGateway
import java.io.InputStream

class GlideSongFetcher(
    context: Context,
    mediaId: MediaId,
    private val lastFmGateway: LastFmGateway

) : BaseDataFetcher(context) {

    private val id = mediaId.resolveId

    override suspend fun execute(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>): String {
        return lastFmGateway.getTrackImage(id)!!
    }

    override suspend fun shouldFetch(): Boolean {
        return lastFmGateway.shouldFetchTrackImage(id)
    }

    override val threshold: Long = 600L
}