package dev.olog.msc.presentation.base

import android.widget.ImageView
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dev.olog.msc.core.MediaId
import dev.olog.msc.imageprovider.CoverUtils
import dev.olog.msc.imageprovider.glide.GlideApp
import dev.olog.msc.presentation.base.ripple.RippleTarget

object BaseBindingAdapter {

    fun loadImageImpl(
        view: ImageView,
        mediaId: MediaId,
        override: Int,
        priority: Priority = Priority.HIGH,
        crossfade: Boolean = true
    ) {

        val context = view.context

        GlideApp.with(context).clear(view)

//        val load: Any = if (ImagesFolderUtils.isChoosedImage(item.image)) {
//            item.image
//        } else item TODO check

        var builder = GlideApp.with(context)
            .load(mediaId)
            .override(override)
            .priority(priority)
            .placeholder(CoverUtils.getGradient(context, mediaId))
        if (crossfade) {
            builder = builder.transition(DrawableTransitionOptions.withCrossFade())
        }
        builder.into(RippleTarget(view, mediaId.isLeaf))
    }
}