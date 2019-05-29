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
import dev.olog.msc.imageprovider.di.inject
import dev.olog.msc.imageprovider.glide.loader.GlideLastFmImageLoader
import dev.olog.msc.imageprovider.glide.loader.GlideMergedImageLoader
import dev.olog.msc.imageprovider.glide.loader.GlideOriginalImageLoader
import dev.olog.msc.imageprovider.glide.loader.GlideOverridenImageLoader
import java.io.InputStream
import javax.inject.Inject

@GlideModule
@Keep
class GlideModule : AppGlideModule() {

    @Inject
    internal lateinit var lastFmFactory: GlideLastFmImageLoader.Factory
    @Inject
    internal lateinit var originalFactory: GlideOriginalImageLoader.Factory
    @Inject
    internal lateinit var mergedFactory: GlideMergedImageLoader.Factory
    @Inject
    internal lateinit var overrideFactory: GlideOverridenImageLoader.Factory

    private var injected = false

    private fun injectIfNeeded(context: Context) {
        if (!injected) {
            injected = true
            inject(context)
        }
    }

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
        injectIfNeeded(context)

        registry.prepend(MediaId::class.java, InputStream::class.java, lastFmFactory)
        registry.prepend(MediaId::class.java, InputStream::class.java, mergedFactory)
        registry.prepend(MediaId::class.java, InputStream::class.java, originalFactory)
        registry.prepend(MediaId::class.java, InputStream::class.java, overrideFactory)

        // TODO check if has to be prepend or append
        registry.append(AudioFileCover::class.java, InputStream::class.java, AudioFileCoverLoader.Factory())
    }

    override fun isManifestParsingEnabled(): Boolean = false

}