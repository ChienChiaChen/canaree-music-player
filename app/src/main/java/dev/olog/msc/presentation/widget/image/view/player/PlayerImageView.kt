package dev.olog.msc.presentation.widget.image.view.player

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.util.AttributeSet
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.Target
import dev.olog.msc.glide.GlideApp
import dev.olog.msc.ripple.RippleTarget
import dev.olog.msc.glide.creator.CoverUtils
import dev.olog.msc.utils.k.extension.getMediaId

open class PlayerImageView @JvmOverloads constructor(
        context: Context,
        attr: AttributeSet? = null

) : AdaptiveColorImageView(context, attr) {


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
