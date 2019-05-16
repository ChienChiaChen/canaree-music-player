package dev.olog.msc.presentation.base.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import dev.olog.msc.presentation.base.model.BaseModel

abstract class BasePagedAdapter<Model : BaseModel>(diffCallback: DiffUtil.ItemCallback<Model>) :
    PagedListAdapter<Model, DataBoundViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            viewType,
            parent,
            false
        )
        val viewHolder = DataBoundViewHolder(binding)
        initViewHolderListeners(viewHolder, viewType)
        return viewHolder
    }

    fun tryGetItem(position: Int): Model? {
        return getItem(position)
    }

    protected abstract fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int)

    override fun onBindViewHolder(holder: DataBoundViewHolder, position: Int) {
        getItem(position)?.let { model ->
            bind(holder.binding, model, position)
            holder.binding.executePendingBindings()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.type ?: -1 // TODO loading placeholder
    }

    fun elementAt(position: Int): Model? {
        return currentList?.get(position)
    }

    protected abstract fun bind(binding: ViewDataBinding, item: Model, position: Int)

    @CallSuper
    override fun onViewAttachedToWindow(holder: DataBoundViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onAppear()
    }

    @CallSuper
    override fun onViewDetachedFromWindow(holder: DataBoundViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onDisappear()
    }

}