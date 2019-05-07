package dev.olog.presentation.base

import android.widget.ImageView
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dev.olog.msc.imageprovider.CoverUtils
import dev.olog.msc.imageprovider.GlideApp
import dev.olog.msc.imageprovider.ImageModel
import dev.olog.msc.imageprovider.ImagesFolderUtils
import dev.olog.presentation.base.ripple.RippleTarget

object BaseBindingAdapter {

    fun loadImageImpl(
            view: ImageView,
            item: ImageModel,
            override: Int,
            priority: Priority = Priority.HIGH,
            crossfade: Boolean = true) {

        val mediaId = item.mediaId
        val context = view.context

        GlideApp.with(context).clear(view)

        val load: Any = if (ImagesFolderUtils.isChoosedImage(item.image)) {
            item.image
        } else item

        var builder = GlideApp.with(context)
                .load(load)
                .override(override)
                .priority(priority)
                .placeholder(CoverUtils.getGradient(context, mediaId))
        if (crossfade) {
            builder = builder.transition(DrawableTransitionOptions.withCrossFade())
        }
        builder.into(RippleTarget(view, mediaId.isLeaf))
    }
}