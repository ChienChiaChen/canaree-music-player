package dev.olog.msc.presentation.detail.adapter

import androidx.databinding.ViewDataBinding
import dev.olog.msc.presentation.base.adapter.BasePagedAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.base.extensions.elevateSongOnTouch
import dev.olog.msc.presentation.base.extensions.setOnClickListener
import dev.olog.msc.presentation.base.extensions.setOnLongClickListener
import dev.olog.msc.presentation.base.interfaces.MediaProvider
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.detail.BR
import dev.olog.msc.presentation.detail.R
import dev.olog.msc.presentation.navigator.Navigator
import kotlinx.android.synthetic.main.item_detail_song_most_played.view.*

internal class DetailMostPlayedAdapter(
    private val navigator: Navigator

) : BasePagedAdapter<DisplayableItem>(DiffCallbackMostPlayed) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            val mediaProvider = viewHolder.itemView.context as MediaProvider
            mediaProvider.playMostPlayed(item.mediaId)
        }

        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView)
        }

        viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
            navigator.toDialog(item.mediaId, view)
        }
        viewHolder.elevateSongOnTouch()
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.position, position)
    }

    override fun onBindViewHolder(holder: DataBoundViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()){
            holder.itemView.index.text = (payloads[0] as Int + 1).toString()
        } else {
            super.onBindViewHolder(holder, position, payloads)

        }
    }

}