package dev.olog.msc.presentation.tabs.foldertree

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle

import dev.olog.msc.core.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.presentation.base.adapter.AbsAdapter
import dev.olog.presentation.base.adapter.DataBoundViewHolder
import dev.olog.presentation.base.extensions.setOnClickListener
import dev.olog.presentation.base.extensions.setOnLongClickListener
import dev.olog.presentation.base.interfaces.MediaProvider

class FolderTreeFragmentAdapter(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val viewModel: FolderTreeFragmentViewModel,
        private val navigator: Navigator

) : AbsAdapter<DisplayableFile>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_folder_tree_directory,
            R.layout.item_folder_tree_track -> {
                viewHolder.setOnClickListener(controller) { item, _, _ ->
                    when {
                        item.mediaId == FolderTreeFragmentViewModel.BACK_HEADER_ID -> viewModel.goBack()
                        item.isFile() && item.asFile().isDirectory -> viewModel.nextFolder(item.asFile())
                        else -> {
                            val mediaProvider = viewHolder.itemView.context as MediaProvider
                            viewModel.createMediaId(item)?.let { mediaId ->
                                mediaProvider.playFromMediaId(mediaId, null)
                            }

                        }
                    }
                }
                viewHolder.setOnLongClickListener(controller) { item, _, view ->
                    if (item.mediaId == FolderTreeFragmentViewModel.BACK_HEADER_ID) {
                        return@setOnLongClickListener
                    }
                    if (!item.asFile().isDirectory) {
                        viewModel.createMediaId(item)?.let { mediaId ->
                            navigator.toDialog(mediaId, view)
                        }
                    }
                }
            }
        }

    }

    override fun bind(binding: ViewDataBinding, item: DisplayableFile, position: Int) {
        binding.setVariable(BR.item, item)
    }
}