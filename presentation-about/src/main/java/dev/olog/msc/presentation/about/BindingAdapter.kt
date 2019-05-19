package dev.olog.msc.presentation.about

import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import dev.olog.msc.imageprovider.glide.GlideApp
import dev.olog.msc.presentation.about.thanks.SpecialThanksModel

object BindingAdapter {
    @BindingAdapter("imageSpecialThanks")
    @JvmStatic
    fun loadSongImage(view: ImageView, item: SpecialThanksModel) {
        GlideApp.with(view)
                .load(ContextCompat.getDrawable(view.context, item.image))
                .into(view)
    }
}