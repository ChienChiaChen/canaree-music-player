package dev.olog.msc.imageprovider.glide.fetcher

import android.content.Context
import com.bumptech.glide.Priority
import com.bumptech.glide.load.data.DataFetcher
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.PrefsKeys
import dev.olog.msc.core.gateway.LastFmGateway
import java.io.InputStream

class GlideAlbumFetcher(
    context: Context,
    mediaId: MediaId,
    private val lastFmGateway: LastFmGateway,
    prefsKeys: PrefsKeys

) : BaseRxDataFetcher(context, prefsKeys) {

    private val id = mediaId.resolveId

    override suspend fun execute(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>): String {
        return lastFmGateway.getAlbum(id)!!.image
    }


    override suspend fun shouldFetch(): Boolean {
        return lastFmGateway.shouldFetchAlbum(id)
    }

    override val threshold: Long = 600
}