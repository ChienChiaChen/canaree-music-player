package dev.olog.msc.imageprovider.glide.loader

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.core.gateway.podcast.PodcastGateway
import dev.olog.msc.core.gateway.track.SongGateway
import dev.olog.msc.imageprovider.glide.fetcher.GlideOverridenImageFetcher
import java.io.InputStream
import javax.inject.Inject

internal class GlideOverridenImageLoader(
    private val usedImageGateway: UsedImageGateway,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway
) : ModelLoader<MediaId, InputStream> {

    override fun buildLoadData(
        mediaId: MediaId,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {
        return ModelLoader.LoadData(
            MediaIdKey(mediaId),
            GlideOverridenImageFetcher(
                mediaId,
                usedImageGateway,
                songGateway,
                podcastGateway
            )
        )
    }

    override fun handles(mediaId: MediaId): Boolean {
        return mediaId.isLeaf || mediaId.isAlbum || mediaId.isArtist ||
                mediaId.isPodcastAlbum || mediaId.isPodcastArtist
    }

    class Factory @Inject constructor(
        private val usedImageGateway: UsedImageGateway,
        private val songGateway: SongGateway,
        private val podcastGateway: PodcastGateway

    ) : ModelLoaderFactory<MediaId, InputStream> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<MediaId, InputStream> {
            return GlideOverridenImageLoader(
                usedImageGateway, songGateway, podcastGateway
            )
        }

        override fun teardown() {

        }
    }

}

