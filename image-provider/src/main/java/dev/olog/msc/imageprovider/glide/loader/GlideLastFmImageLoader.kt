package dev.olog.msc.imageprovider.glide.loader

import android.content.Context
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.PrefsKeys
import dev.olog.msc.core.gateway.LastFmGateway
import dev.olog.msc.imageprovider.glide.fetcher.GlideAlbumFetcher
import dev.olog.msc.imageprovider.glide.fetcher.GlideArtistFetcher
import dev.olog.msc.imageprovider.glide.fetcher.GlideSongFetcher
import java.io.InputStream

internal class GlideLastFmImageLoader(
    private val context: Context,
    private val lastFmGateway: LastFmGateway,
    private val prefsKeys: PrefsKeys

) : ModelLoader<MediaId, InputStream> {

    override fun handles(mediaId: MediaId): Boolean {
        if (mediaId.isAnyPodcast) {
            return false
        }
        return mediaId.isLeaf || mediaId.isAlbum || mediaId.isArtist
    }

    override fun buildLoadData(
        mediaId: MediaId,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {

        return if (mediaId.isLeaf) {
            // download track image
            ModelLoader.LoadData(
                MediaIdKey(mediaId),
                GlideSongFetcher(
                    context,
                    mediaId,
                    lastFmGateway,
                    prefsKeys
                )
            )
        } else if (mediaId.isAlbum) {
            // download album image
            ModelLoader.LoadData(
                MediaIdKey(mediaId),
                GlideAlbumFetcher(
                    context,
                    mediaId,
                    lastFmGateway,
                    prefsKeys
                )
            )
        } else {
            // download artist image
            ModelLoader.LoadData(
                MediaIdKey(mediaId),
                GlideArtistFetcher(
                    context,
                    mediaId,
                    lastFmGateway,
                    prefsKeys
                )
            )
        }
    }

    class Factory(
        private val context: Context,
        private val lastFmGateway: LastFmGateway,
        private val prefsKeys: PrefsKeys

    ) : ModelLoaderFactory<MediaId, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<MediaId, InputStream> {
            return GlideLastFmImageLoader(
                context,
                lastFmGateway,
                prefsKeys
            )
        }

        override fun teardown() {
        }
    }

}