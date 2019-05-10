package dev.olog.msc.presentation.tabs.foldertree

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dev.olog.msc.core.MediaId
import dev.olog.msc.imageprovider.CoverUtils
import dev.olog.msc.imageprovider.GlideApp
import dev.olog.msc.imageprovider.ImageModel
import dev.olog.msc.imageprovider.ImagesFolderUtils
import dev.olog.msc.presentation.base.BaseBindingAdapter
import dev.olog.msc.presentation.tabs.foldertree.model.AudioFileCover

object BindingAdapter {
    private const val OVERRIDE_SMALL = 150

    @JvmStatic
    @BindingAdapter("fileTrackLoader")
    fun loadFile(view: ImageView, item: DisplayableFile){
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
    fun loadDirImage(view: ImageView, item: DisplayableFile){
        val path = item.path ?: ""
        val displayableItem = ImageModel(MediaId.folderId(path), ImagesFolderUtils.forFolder(view.context, path))
        BaseBindingAdapter.loadImageImpl(view, displayableItem, OVERRIDE_SMALL)
    }
}