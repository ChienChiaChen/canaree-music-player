package dev.olog.msc

import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import dev.olog.msc.imageprovider.GlideApp
import dev.olog.msc.presentation.special.thanks.SpecialThanksModel
import dev.olog.presentation.base.model.DisplayableItem
import dev.olog.presentation.base.widgets.image.view.QuickActionView

object BindingsAdapter {

    @BindingAdapter("imageSpecialThanks")
    @JvmStatic
    fun loadSongImage(view: ImageView, item: SpecialThanksModel) {
        GlideApp.with(view)
                .load(ContextCompat.getDrawable(view.context, item.image))
                .into(view)
    }

    @BindingAdapter("quickActionItem")
    @JvmStatic
    fun quickActionItem(view: QuickActionView, item: DisplayableItem){
        view.setId(item.mediaId)
    }

}