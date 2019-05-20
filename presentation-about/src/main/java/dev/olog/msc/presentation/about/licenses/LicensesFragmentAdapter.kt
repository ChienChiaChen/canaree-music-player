package dev.olog.msc.presentation.about.licenses

import android.text.method.LinkMovementMethod
import androidx.databinding.ViewDataBinding
import dev.olog.msc.presentation.about.BR
import dev.olog.msc.presentation.about.databinding.ItemLicenseBinding
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.base.adapter.SimpleAdapter
import kotlinx.android.synthetic.main.item_license.view.*

class LicensesFragmentAdapter : SimpleAdapter<LicenseModel>() {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        (viewHolder.binding as ItemLicenseBinding)
            .url.movementMethod = LinkMovementMethod.getInstance()

        viewHolder.itemView.setOnClickListener {
            val maxLines = if (viewHolder.itemView.license.maxLines > 10) 10 else Int.MAX_VALUE
            viewHolder.itemView.license.maxLines = maxLines
        }
    }

    override fun bind(binding: ViewDataBinding, item: LicenseModel, position: Int) {
        binding.setVariable(BR.licenseModel, item)
    }

}