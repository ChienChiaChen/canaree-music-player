package dev.olog.msc.presentation.about.thanks

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import dev.olog.msc.presentation.base.BR
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder

class SpecialThanksFragmentAdapter(lifecycle: Lifecycle)
    : AbsAdapter<SpecialThanksModel>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
    }

    override fun bind(binding: ViewDataBinding, item: SpecialThanksModel, position: Int) {
        binding.setVariable(BR.item,  item)
    }

}