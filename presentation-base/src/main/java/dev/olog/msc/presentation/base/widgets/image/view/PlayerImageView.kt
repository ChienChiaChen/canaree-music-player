package dev.olog.msc.presentation.base.widgets.image.view

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.util.AttributeSet
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.Target
import dev.olog.msc.imageprovider.CoverUtils
import dev.olog.msc.imageprovider.glide.GlideApp
import dev.olog.msc.presentation.base.ripple.RippleTarget
import dev.olog.msc.presentation.base.utils.getMediaId
import dev.olog.msc.shared.ui.imageview.adaptive.AdaptiveColorImageView

open class PlayerImageView(context: Context, attrs: AttributeSet?) : AdaptiveColorImageView(context, attrs) {

    init {
        isClickable = true
        isFocusable = true
    }

    open fun loadImage(metadata: MediaMetadataCompat) {
        val mediaId = metadata.getMediaId()

        GlideApp.with(context).clear(this)
        GlideApp.with(context)
                .load(mediaId)
                .placeholder(CoverUtils.getGradient(context, mediaId))
                .priority(Priority.IMMEDIATE)
                .transition(DrawableTransitionOptions.withCrossFade())
                .override(Target.SIZE_ORIGINAL)
                .into(RippleTarget(this))
    }

}
