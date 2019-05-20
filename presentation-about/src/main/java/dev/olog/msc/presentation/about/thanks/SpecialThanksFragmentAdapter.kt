package dev.olog.msc.presentation.about.thanks

import androidx.databinding.ViewDataBinding
import dev.olog.msc.presentation.base.BR
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.base.adapter.SimpleAdapter

class SpecialThanksFragmentAdapter : SimpleAdapter<SpecialThanksModel>() {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
    }

    override fun bind(binding: ViewDataBinding, item: SpecialThanksModel, position: Int) {
        binding.setVariable(BR.item, item)
    }

}