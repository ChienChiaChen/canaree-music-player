package dev.olog.msc.presentation.playing.queue

import android.graphics.Typeface
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import dev.olog.msc.imageprovider.ImageModel
import dev.olog.msc.presentation.base.BaseBindingAdapter
import dev.olog.msc.presentation.playing.queue.model.DisplayableQueueSong

object BindingAdapter {

    private const val OVERRIDE_SIZE = 150

    @BindingAdapter("imageSong")
    @JvmStatic
    fun loadSongImage(view: ImageView, item: DisplayableQueueSong) {
        BaseBindingAdapter.loadImageImpl(view, ImageModel(item.mediaId, item.image), OVERRIDE_SIZE)
    }

    @BindingAdapter("setBoldIfTrue")
    @JvmStatic
    fun setBoldIfTrue(view: TextView, setBold: Boolean){
        val style = if (setBold) Typeface.BOLD else Typeface.NORMAL
        view.setTypeface(null, style)
    }

}