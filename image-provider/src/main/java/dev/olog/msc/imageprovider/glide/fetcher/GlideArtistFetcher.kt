package dev.olog.msc.imageprovider.glide.fetcher

import android.content.Context
import com.bumptech.glide.Priority
import com.bumptech.glide.load.data.DataFetcher
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.PrefsKeys
import dev.olog.msc.core.gateway.LastFmGateway
import java.io.InputStream

// for some reason last fm for some artists (maybe all) is returning a start instead of the artist image, this
// is the name of the image
private const val LAST_FM_PLACEHOLDER = "2a96cbd8b46e442fc41c2b86b821562f.png"

class GlideArtistFetcher(
    context: Context,
    mediaId: MediaId,
    private val lastFmGateway: LastFmGateway,
    prefsKeys: PrefsKeys

) : BaseDataFetcher(context, prefsKeys) {

    private val id = mediaId.resolveId

    override suspend fun execute(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>): String {
        val image = lastFmGateway.getArtist(id)!!.image
        if (image.endsWith(LAST_FM_PLACEHOLDER)) {
            return ""
        }
        return image
    }

    override suspend fun shouldFetch(): Boolean {
        return lastFmGateway.shouldFetchArtist(id)
    }

    override val threshold: Long = 250
}