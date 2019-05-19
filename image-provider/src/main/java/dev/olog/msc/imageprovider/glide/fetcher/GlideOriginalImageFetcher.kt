package dev.olog.msc.imageprovider.glide.fetcher

import android.media.MediaMetadataRetriever
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.gateway.podcast.PodcastGateway
import dev.olog.msc.core.gateway.track.SongGateway
import kotlinx.coroutines.*
import java.io.*

private val FALLBACKS = arrayOf("cover.jpg", "album.jpg", "folder.jpg", "cover.png", "album.png", "folder.png")


class GlideOriginalImageFetcher(
    private val mediaId: MediaId,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway

) : DataFetcher<InputStream> {

    private var job : Job? = null

    override fun getDataClass(): Class<InputStream> = InputStream::class.java
    override fun getDataSource(): DataSource = DataSource.LOCAL

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        job = GlobalScope.launch(Dispatchers.IO) { // TODO is needed to launch a coroutine?
            val id = getId()
            if (id == -1L) {
                callback.onLoadFailed(Exception("item not found for id$id"))
                return@launch
            }
            val itemPath: String?

            itemPath = when {
                mediaId.isLeaf && !mediaId.isPodcast -> songGateway.getByParam(id).getItem()?.path
                mediaId.isLeaf && mediaId.isPodcast -> podcastGateway.getByParam(id).getItem()?.path
                mediaId.isAlbum -> songGateway.getByAlbumId(id).getItem()?.path
                mediaId.isPodcastAlbum -> podcastGateway.getByAlbumId(id).getItem()?.path
                else -> {
                    callback.onLoadFailed(IllegalArgumentException("not a valid media id=$mediaId"))
                    return@launch
                }
            }
            yield()

            if (itemPath == null) {
                callback.onLoadFailed(IllegalArgumentException("track not found for id $id"))
                return@launch
            }
            try {
                val stream = loadImage(itemPath)
                callback.onDataReady(stream)
            } catch (ex: Exception) {
                callback.onLoadFailed(ex)
            }
        }
    }

    private suspend fun loadImage(path: String): InputStream? {
        val retriever = MediaMetadataRetriever()
        yield()
        return try {
            retriever.setDataSource(path) // time consuming
            yield()
            val picture = retriever.embeddedPicture
            yield()
            if (picture != null) {
                ByteArrayInputStream(picture)
            } else {
                fallback(path)
            }
        } finally {
            retriever.release()
        }
    }

    private suspend fun fallback(path: String): InputStream? {
        try {
            val mp3File = MP3File(path)
            if (mp3File.hasID3v2Tag()) {
                val art = mp3File.tag.firstArtwork
                if (art != null) {
                    val data = art.binaryData
                    return ByteArrayInputStream(data)
                }
            }
        } catch (ex: ReadOnlyFileException) {
        } catch (ex: InvalidAudioFrameException) {
        } catch (ex: TagException) {
        } catch (ex: IOException) {
        }

        val parent = File(path).parentFile
        for (fallback in FALLBACKS) {
            yield()
            val cover = File(parent, fallback)
            if (cover.exists()) {
                return FileInputStream(cover)
            }
        }
        return null
    }

    private fun getId(): Long {
        var trackId = -1L
        if (mediaId.isLeaf) {
            trackId = mediaId.leaf!!
        } else if (mediaId.isAlbum || mediaId.isPodcastAlbum) {
            trackId = mediaId.categoryId
        }
        return trackId
    }

    override fun cleanup() {
        job?.cancel()
    }

    override fun cancel() {
        job?.cancel()
    }

}