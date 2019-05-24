package dev.olog.msc.presentation.base

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.Target
import dev.olog.msc.core.MediaId
import dev.olog.msc.imageprovider.CoverUtils
import dev.olog.msc.imageprovider.glide.GlideApp
import dev.olog.msc.presentation.base.ripple.RippleTarget
import dev.olog.msc.presentation.base.widgets.image.view.QuickActionView

object DisplayableItemBindingAdapter {

    private const val OVERRIDE_SMALL = 150
    private const val OVERRIDE_MID = 400

    @JvmStatic
    private fun loadImageImpl(
            view: ImageView,
            mediaId: MediaId,
            override: Int,
            priority: Priority = Priority.HIGH,
            crossfade: Boolean = true
    ) {

        val context = view.context

        GlideApp.with(context).clear(view)

        var builder = GlideApp.with(context)
                .load(mediaId)
                .override(override)
                .priority(priority)
                .placeholder(CoverUtils.getGradient(context, mediaId))
        if (crossfade) {
            builder = builder.transition(DrawableTransitionOptions.withCrossFade())
        }
        if (mediaId.isLeaf) {
            builder.into(view)

        } else {
            builder.into(RippleTarget(view))

        }
    }

    @BindingAdapter("imageSong")
    @JvmStatic
    fun loadSongImage(view: ImageView, mediaId: MediaId) {
        loadImageImpl(view, mediaId, OVERRIDE_SMALL)
    }

    @BindingAdapter("imageAlbum")
    @JvmStatic
    fun loadAlbumImage(view: ImageView, mediaId: MediaId) {
        loadImageImpl(view, mediaId, OVERRIDE_MID, Priority.HIGH)
    }

    @JvmStatic
    fun loadBigAlbumImage(view: ImageView, mediaId: MediaId) {
        loadImageImpl(
                view,
                mediaId,
                Target.SIZE_ORIGINAL,
                Priority.IMMEDIATE,
                crossfade = false
        )
    }

    @BindingAdapter("quickActionItem")
    @JvmStatic
    fun quickActionItem(view: QuickActionView, mediaId: MediaId) {
        view.setId(mediaId)
    }
}