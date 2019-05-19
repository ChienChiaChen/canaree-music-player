package dev.olog.msc.imageprovider.glide

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.executor.GlideExecutor
import com.bumptech.glide.load.engine.executor.GlideExecutor.UncaughtThrowableStrategy.IGNORE
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.PrefsKeys
import dev.olog.msc.core.gateway.LastFmGateway
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.core.gateway.podcast.PodcastGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.track.FolderGateway
import dev.olog.msc.core.gateway.track.GenreGateway
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.core.gateway.track.SongGateway
import dev.olog.msc.imageprovider.glide.loader.GlideLastFmImageLoader
import dev.olog.msc.imageprovider.glide.loader.GlideMergedImageLoader
import dev.olog.msc.imageprovider.glide.loader.GlideOriginalImageLoader
import dev.olog.msc.imageprovider.glide.loader.GlideOverridenImageLoader
import java.io.InputStream

@GlideModule
@Keep
class GlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {

        builder.setLogLevel(Log.ERROR)
            .setDefaultRequestOptions(defaultRequestOptions(context))
            .setDiskCacheExecutor(GlideExecutor.newDiskCacheExecutor(IGNORE))
            .setSourceExecutor(GlideExecutor.newSourceExecutor(IGNORE))
    }

    private fun defaultRequestOptions(context: Context): RequestOptions {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        return RequestOptions()
            // Prefer higher quality images unless we're on a low RAM device
            .format(
                if (activityManager.isLowRamDevice)
                    DecodeFormat.PREFER_RGB_565 else DecodeFormat.PREFER_ARGB_8888
            ).disallowHardwareConfig()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .centerCrop()
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val lastFmGateway = context.getClass<LastFmGateway>("lastFmGateway")
        val songGateway = context.getClass<SongGateway>("songGateway")
        val podcastGateway = context.getClass<PodcastGateway>("podcastGateway")
        val folderGateway = context.getClass<FolderGateway>("folderGateway")
        val genreGateway = context.getClass<GenreGateway>("genreGateway")
        val playlistGateway = context.getClass<PlaylistGateway>("playlistGateway")
        val prefsGateway = context.getClass<AppPreferencesGateway>("prefsGateway")
        val usedImageGateway = context.getClass<UsedImageGateway>("usedImageGateway")
        val prefsKeys = context.getClass<PrefsKeys>("prefsKeys")

        val lastFmFactory =
            GlideLastFmImageLoader.Factory(context, lastFmGateway, prefsKeys)
        val originalFactory =
            GlideOriginalImageLoader.Factory(songGateway, podcastGateway)
        val mergedFactory = GlideMergedImageLoader.Factory(context, folderGateway,
            playlistGateway, genreGateway, prefsGateway)
        val overrideFactory = GlideOverridenImageLoader.Factory(
            usedImageGateway, songGateway, podcastGateway
        )

        registry.prepend(MediaId::class.java, InputStream::class.java, lastFmFactory)
        registry.prepend(MediaId::class.java, InputStream::class.java, mergedFactory)
        registry.prepend(MediaId::class.java, InputStream::class.java, originalFactory)
        registry.prepend(MediaId::class.java, InputStream::class.java, overrideFactory)

        // TODO check if has to be prepend or append
        registry.append(AudioFileCover::class.java, InputStream::class.java, AudioFileCoverLoader.Factory())
    }

    private fun <T> Context.getClass(className: String): T {
        // TODO test performance
        return javaClass.getDeclaredField(className).let {
            it.isAccessible = true
            val value = it.get(this)
            @Suppress("UNCHECKED_CAST")
            return@let value as T
        }
    }

    override fun isManifestParsingEnabled(): Boolean = false

}