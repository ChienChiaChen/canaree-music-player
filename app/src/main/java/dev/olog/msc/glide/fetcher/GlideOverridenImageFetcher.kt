package dev.olog.msc.glide.fetcher

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.gateway.PodcastGateway
import dev.olog.msc.core.gateway.SongGateway
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.glide.executor.GlideScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.awaitFirst
import java.io.File
import java.io.InputStream

internal class GlideOverridenImageFetcher(
    private val mediaId: MediaId,
    private val usedImageGateway: UsedImageGateway,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway
) : DataFetcher<InputStream>, CoroutineScope by GlideScope() {

    override fun getDataClass(): Class<InputStream> = InputStream::class.java

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        launch {
            val inputStream: InputStream?
            if (mediaId.isLeaf) {
                inputStream = loadForSongs(mediaId)
            } else if (mediaId.isAlbum || mediaId.isPodcastAlbum) {
                inputStream = loadForAlbums(mediaId)
            } else if (mediaId.isArtist || mediaId.isPodcastArtist) {
                inputStream = loadForArtist(mediaId)
            } else {
                inputStream = null
            }
            callback.onDataReady(inputStream)
        }
    }

    private suspend fun loadForSongs(mediaId: MediaId): InputStream? {
        val trackImage = usedImageGateway.getForTrack(mediaId.resolveId)
        if (trackImage == null) {
            val albumId = if (mediaId.isPodcast) {
                podcastGateway.getByParam(mediaId.resolveId).awaitFirst()?.albumId
            } else {
                songGateway.getByParam(mediaId.resolveId).awaitFirst()?.albumId
            }
            if (albumId != null) {
                val albumImage = usedImageGateway.getForAlbum(albumId)
                if (albumImage != null) {
                    return File(albumImage).inputStream()
                }

            }
            return null

        } else {
            return File(trackImage).inputStream()
        }
    }

    private fun loadForAlbums(mediaId: MediaId): InputStream? {
        val albumImage = usedImageGateway.getForAlbum(mediaId.categoryId)
        if (albumImage != null) {
            return File(albumImage).inputStream()
        }
        return null
    }

    private fun loadForArtist(mediaId: MediaId): InputStream? {
        val artistImage = usedImageGateway.getForArtist(mediaId.categoryId)
        if (artistImage != null) {
            return File(artistImage).inputStream()
        }
        return null
    }

    override fun cleanup() {

    }

    override fun getDataSource(): DataSource = DataSource.LOCAL

    override fun cancel() {

    }
}