package dev.olog.msc.presentation.tabs.foldertree

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dev.olog.msc.core.MediaId
import dev.olog.msc.imageprovider.CoverUtils
import dev.olog.msc.imageprovider.glide.AudioFileCover
import dev.olog.msc.imageprovider.glide.GlideApp
import dev.olog.msc.presentation.base.BaseBindingAdapter

object BindingAdapter {
    private const val OVERRIDE_SMALL = 150

    @JvmStatic
    @BindingAdapter("fileTrackLoader")
    fun loadFile(view: ImageView, item: DisplayableFile) {
        val context = view.context
        GlideApp.with(context).clear(view)

        GlideApp.with(context)
            .load(AudioFileCover(item.path!!))
            .override(OVERRIDE_SMALL)
            .placeholder(CoverUtils.getGradient(context, MediaId.songId(item.path.hashCode().toLong())))
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }

    @JvmStatic
    @BindingAdapter("fileDirLoader")
    fun loadDirImage(view: ImageView, item: DisplayableFile) {
        val path = item.path ?: ""
        BaseBindingAdapter.loadImageImpl(view, MediaId.folderId(path), OVERRIDE_SMALL)
    }
}