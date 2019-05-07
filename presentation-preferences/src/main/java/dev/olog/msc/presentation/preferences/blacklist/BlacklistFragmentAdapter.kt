package dev.olog.msc.presentation.preferences.blacklist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import dev.olog.presentation.base.BR
import dev.olog.presentation.base.adapter.DataBoundViewHolder
import javax.inject.Inject

class BlacklistFragmentAdapter @Inject constructor()
    : androidx.recyclerview.widget.RecyclerView.Adapter<DataBoundViewHolder>() {

    val data: MutableList<BlacklistModel> = mutableListOf()

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int = data[position].displayableItem.type

    override fun onBindViewHolder(holder: DataBoundViewHolder, position: Int) {
        holder.binding.setVariable(BR.item, data[position].displayableItem)
        holder.binding.setVariable(BR.isBlacklisted, data[position].isBlacklisted)
        holder.binding.executePendingBindings()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, viewType, parent, false)
        val viewHolder = DataBoundViewHolder(binding)
        initViewHolderListeners(viewHolder)
        return viewHolder
    }

    private fun initViewHolderListeners(viewHolder: DataBoundViewHolder) {
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            val item = data[position]
            item.isBlacklisted = !item.isBlacklisted
            notifyItemChanged(position)
        }
    }

    fun updateDataSet(list: List<BlacklistModel>){
        this.data.clear()
        this.data.addAll(list)
        notifyDataSetChanged()
    }

}