package dev.olog.msc.presentation.tabs.foldertree

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.base.adapter.ObservableAdapter
import dev.olog.msc.presentation.base.interfaces.MediaProvider
import dev.olog.msc.presentation.navigator.Navigator

class FolderTreeFragmentAdapter(
    lifecycle: Lifecycle,
    private val viewModel: FolderTreeFragmentViewModel,
    private val navigator: Navigator

) : ObservableAdapter<DisplayableFile>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_folder_tree_directory,
            R.layout.item_folder_tree_track -> {
                viewHolder.itemView.setOnClickListener {
                    val item = getItem(viewHolder.adapterPosition)
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
                viewHolder.itemView.setOnLongClickListener { view ->
                    val item = getItem(viewHolder.adapterPosition)
                    if (item.mediaId == FolderTreeFragmentViewModel.BACK_HEADER_ID) {
                        return@setOnLongClickListener false
                    }
                    if (!item.asFile().isDirectory) {
                        viewModel.createMediaId(item)?.let { mediaId ->
                            navigator.toDialog(mediaId, view)
                        }
                    }
                    return@setOnLongClickListener true
                }
            }
        }

    }

    override fun bind(binding: ViewDataBinding, item: DisplayableFile, position: Int) {
        binding.setVariable(BR.item, item)
    }
}