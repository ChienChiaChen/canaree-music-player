package dev.olog.msc.presentation.base

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.Target
import dev.olog.msc.imageprovider.CoverUtils
import dev.olog.msc.imageprovider.GlideApp
import dev.olog.msc.imageprovider.ImageModel
import dev.olog.msc.imageprovider.ImagesFolderUtils
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.base.ripple.RippleTarget
import dev.olog.msc.presentation.base.widgets.image.view.QuickActionView

object DisplayableItemBindingAdapter {

    private const val OVERRIDE_SMALL = 150
    private const val OVERRIDE_MID = 400

    @JvmStatic
    private fun loadImageImpl(
            view: ImageView,
            item: ImageModel,
            override: Int,
            priority: Priority = Priority.HIGH,
            crossfade: Boolean = true){

        val mediaId = item.mediaId
        val context = view.context

        GlideApp.with(context).clear(view)

        val load: Any = if (ImagesFolderUtils.isChoosedImage(item.image)){
            item.image
        } else item

        var builder = GlideApp.with(context)
                .load(load)
                .override(override)
                .priority(priority)
                .placeholder(CoverUtils.getGradient(context, mediaId))
        if (crossfade){
            builder = builder.transition(DrawableTransitionOptions.withCrossFade())
        }
        builder.into(RippleTarget(view, mediaId.isLeaf))
    }

    @BindingAdapter("imageSong")
    @JvmStatic
    fun loadSongImage(view: ImageView, item: DisplayableItem) {
        val model = ImageModel(item.mediaId, item.image)
        loadImageImpl(view, model, OVERRIDE_SMALL)
    }

    @BindingAdapter("imageAlbum")
    @JvmStatic
    fun loadAlbumImage(view: ImageView, item: DisplayableItem) {
        loadImageImpl(view, ImageModel(item.mediaId, item.image), OVERRIDE_MID, Priority.HIGH)
    }

    @BindingAdapter("imageBigAlbum")
    @JvmStatic
    fun loadBigAlbumImage(view: ImageView, item: DisplayableItem) {
        loadImageImpl(view, ImageModel(item.mediaId, item.image), Target.SIZE_ORIGINAL, Priority.IMMEDIATE, crossfade = false)
    }

    @BindingAdapter("quickActionItem")
    @JvmStatic
    fun quickActionItem(view: QuickActionView, item: DisplayableItem){
        view.setId(item.mediaId)
    }
}