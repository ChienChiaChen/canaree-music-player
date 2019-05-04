package dev.olog.msc.glide

import android.content.Context
import com.bumptech.glide.Priority
import com.bumptech.glide.load.data.DataFetcher
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.gateway.LastFmGateway
import io.reactivex.Single
import java.io.InputStream

class GlideArtistFetcher(
        context: Context,
        private val mediaId: MediaId,
        private val lastFmGateway: LastFmGateway

) : BaseRxDataFetcher(context) {

    private val id = mediaId.resolveId

    override fun execute(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>): Single<String> {
        return if (mediaId.isPodcastArtist){
            lastFmGateway.getPodcastArtist(id).map { it.get()!!.image }
        } else {
            lastFmGateway.getArtist(id).map { it.get()!!.image }
        }
    }

    override fun shouldFetch(): Single<Boolean> {
        return if (mediaId.isPodcastArtist){
            lastFmGateway.shouldFetchPodcastArtist(id)
        } else {
            lastFmGateway.shouldFetchArtist(id)
        }
    }

    override val threshold: Long = 250
}